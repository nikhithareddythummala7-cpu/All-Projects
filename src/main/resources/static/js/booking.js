// booking.js - Fixed and resilient booking frontend code

// simple fetch wrapper that sends JWT if present in localStorage or cookie
function getToken() {
    let t = localStorage.getItem("jwtToken");
    if (t) return t;
    const m = document.cookie.match(/(?:^|;)\s*jwt=([^;]+)/);
    return m ? m[1] : null;
}

async function apiRequest(url, opts = {}) {
    opts.headers = opts.headers || {};
    const token = getToken();
    if (token) opts.headers['Authorization'] = 'Bearer ' + token;
    // default to JSON accept
    opts.headers['Accept'] = opts.headers['Accept'] || 'application/json';
    return fetch(url, opts);
}

// increment time string "HH:MM" by hours (integer)
function incrementTime(timeStr, hoursToAdd) {
    if (!timeStr) return timeStr;
    const [hh, mm] = timeStr.split(':').map(Number);
    const d = new Date();
    d.setHours(hh);
    d.setMinutes(mm || 0);
    d.setSeconds(0);
    d.setMilliseconds(0);
    d.setHours(d.getHours() + Number(hoursToAdd || 0));
    const h = String(d.getHours()).padStart(2, '0');
    const m = String(d.getMinutes()).padStart(2, '0');
    return `${h}:${m}`;
}

// Open booking form for selected table (called from template button with onclick="openBookingForm(this)")
function openBookingForm(button) {
    // button needs data-table-id, data-status, data-capacity
    const tableId = button.getAttribute('data-table-id');
    const status = button.getAttribute('data-status');
    const capacity = button.getAttribute('data-capacity');

    // defensive checks
    if (!tableId) {
        alert('Table id missing. Try again.');
        return;
    }

    if (status && String(status).toLowerCase() !== 'available') {
        alert('This table is not available for booking.');
        return;
    }

    // set hidden input & capacity
    const selInput = document.getElementById('selected-table-id');
    if (selInput) selInput.value = tableId;

    const guestsInput = document.getElementById('guests');
    if (guestsInput && capacity) {
        guestsInput.max = capacity;
        // if value > capacity, lower it
        if (Number(guestsInput.value) > Number(capacity)) guestsInput.value = capacity;
    }

    // reveal form
    const container = document.getElementById('booking-form-container');
    if (container) {
        container.style.display = 'block';
        container.scrollIntoView({behavior: 'smooth', block: 'center'});
    }
}

// Close booking form
function cancelBookingForm() {
    const container = document.getElementById('booking-form-container');
    if (container) container.style.display = 'none';
    const form = document.getElementById('booking-form');
    if (form) form.reset();
    const sel = document.getElementById('selected-table-id');
    if (sel) sel.value = '';
}

// Submit booking flow
async function submitBooking(e) {
    if (e && typeof e.preventDefault === 'function') e.preventDefault();

    const form = document.getElementById('booking-form');
    if (!form) { alert('Booking form not found'); return; }

    const formData = new FormData(form);
    const tableId = formData.get('tableId') || document.getElementById('selected-table-id')?.value;
    const date = formData.get('date');
    const time = formData.get('time');
    const guests = Number(formData.get('guests')) || 1;

    if (!tableId || !date || !time || !guests) {
        alert('Please fill in date, time and number of guests.');
        return;
    }

    // validate guests against max if set
    const guestsInput = document.getElementById('guests');
    if (guestsInput && guestsInput.max) {
        const max = Number(guestsInput.max);
        if (max && guests > max) {
            alert(`Number of guests cannot exceed table capacity (${max}).`);
            return;
        }
    }

    // Prepare ISO strings
    const startTime = `${date}T${time}`;
    const endTime = `${date}T${incrementTime(time, 2)}`; // default +2 hours

    // Availability check: query bookings for the table and ensure no overlap
    try {
        const availabilityResp = await apiRequest(`/api/bookings/table/${encodeURIComponent(tableId)}`, {
            method: 'GET'
        });

        if (!availabilityResp.ok) {
            // if unauthorized, prompt login, else show message
            if (availabilityResp.status === 401) {
                alert('Please login to create a booking.');
                window.location.href = '/login';
                return;
            }
            alert('Unable to check table availability. Please try again.');
            return;
        }

        const existingBookings = await availabilityResp.json();
        const start = new Date(startTime);
        const end = new Date(endTime);

        for (const b of existingBookings) {
            // ignore cancelled
            if (b.status && b.status.toUpperCase() === 'CANCELLED') continue;
            // defensive parse
            const bStart = b.startTime ? new Date(b.startTime) : null;
            const bEnd = b.endTime ? new Date(b.endTime) : null;
            if (!bStart || !bEnd) continue;

            // overlap if start < existing.end && end > existing.start
            if (start < bEnd && end > bStart) {
                alert('This time slot is already booked. Please choose a different time.');
                return;
            }
        }
    } catch (err) {
        console.error('Availability check failed', err);
        alert('Error checking availability. Please try again.');
        return;
    }

    // Create booking: backend expects request params on POST (createBooking uses @RequestParam)
    const url = `/api/bookings/create?tableId=${encodeURIComponent(tableId)}&startTime=${encodeURIComponent(startTime)}&endTime=${encodeURIComponent(endTime)}&numberOfGuests=${encodeURIComponent(guests)}`;

    try {
        const resp = await apiRequest(url, { method: 'POST' });

        if (resp.ok) {
            // success: redirect to my-bookings (server side param will show success message)
            window.location.href = '/my-bookings?success=true';
        } else {
            const data = await resp.json().catch(() => ({}));
            const msg = data && data.error ? data.error : `Failed to create booking (status ${resp.status})`;
            alert(msg);
        }
    } catch (err) {
        console.error('Create booking failed', err);
        alert('Error creating booking. Please try again.');
    }
}

// Cancel booking (for user/caller)
async function cancelBooking(bookingId) {
    if (!confirm('Are you sure you want to cancel this booking?')) return;
    try {
        const resp = await apiRequest(`/api/bookings/${encodeURIComponent(bookingId)}/cancel`, {
            method: 'PUT'
        });
        if (resp.ok) {
            alert('Booking cancelled');
            window.location.reload();
        } else {
            const d = await resp.json().catch(()=>({}));
            alert(d.error || 'Failed to cancel booking');
        }
    } catch (err) {
        console.error(err);
        alert('Error cancelling booking');
    }
}

// page init wiring
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('booking-form');
    if (form) form.addEventListener('submit', submitBooking);

    // make buttons with class "open-booking" open the form (if not wired in template)
    document.querySelectorAll('button.open-booking').forEach(btn => {
        btn.addEventListener('click', () => openBookingForm(btn));
    });
});
