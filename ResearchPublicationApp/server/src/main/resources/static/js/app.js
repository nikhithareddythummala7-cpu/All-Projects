// Simple frontend handlers for login, register, and submit publication
(function () {
  const base = ''; // same-origin

  // Helper: show toast-like alerts
  function notify(msg, isError = false) {
    console[isError ? 'error' : 'log'](msg);
    alert(msg);
  }

  // ADMIN: Users page list + activate/deactivate
  const adminUsersTable = document.getElementById('adminUsers');
  if (adminUsersTable) {
    (async () => {
      try {
        const res = await fetch('/api/admin/users');
        const users = await res.json().catch(() => []);
        adminUsersTable.innerHTML = Array.isArray(users) ? users.map(u => `
          <tr>
            <td>${u.username || ''}</td>
            <td>${u.email || ''}</td>
            <td>${u.usertype || ''}</td>
            <td>${u.status || ''}</td>
            <td>
              <button class="btn small" data-activate="${u._id || u.id}">Activate</button>
              <button class="btn small danger" data-deactivate="${u._id || u.id}">Deactivate</button>
            </td>
          </tr>
        `).join('') : '';

        adminUsersTable.addEventListener('click', async (ev) => {
          const a = ev.target.closest('[data-activate]');
          const d = ev.target.closest('[data-deactivate]');
          if (a) {
            const id = a.getAttribute('data-activate');
            const resp = await fetch(`/api/admin/users/${encodeURIComponent(id)}/activate`, { method: 'POST' });
            if (resp.ok) { notify('User activated'); a.closest('tr').querySelector('td:nth-child(4)').textContent = 'active'; }
            else notify('Failed to activate', true);
          }
          if (d) {
            const id = d.getAttribute('data-deactivate');
            const resp = await fetch(`/api/admin/users/${encodeURIComponent(id)}/deactivate`, { method: 'POST' });
            if (resp.ok) { notify('User deactivated'); d.closest('tr').querySelector('td:nth-child(4)').textContent = 'inactive'; }
            else notify('Failed to deactivate', true);
          }
        });
      } catch (err) {
        notify('Failed to load users: ' + err.message, true);
      }
    })();
  }

  // ADMIN: Analytics page
  const metricUsers = document.getElementById('metricUsers');
  const metricPubs = document.getElementById('metricPubs');
  const metricDomains = document.getElementById('metricDomains');
  const metricDownloads = document.getElementById('metricDownloads');
  const byDomainTable = document.getElementById('byDomainTable');
  if (metricUsers && metricPubs && metricDomains && metricDownloads && byDomainTable) {
    (async () => {
      try {
        const res = await fetch('/api/admin/analytics');
        const data = await res.json();
        metricUsers.textContent = data.usersCount ?? '-';
        metricPubs.textContent = data.publicationsCount ?? '-';
        metricDomains.textContent = data.domainsCount ?? '-';
        metricDownloads.textContent = data.downloadsCount ?? '-';
        const rows = Object.entries(data.byDomain || {}).map(([domain, count]) => `
          <tr><td>${domain}</td><td>${count}</td></tr>
        `).join('');
        byDomainTable.innerHTML = rows || '';
      } catch (err) {
        notify('Failed to load analytics: ' + err.message, true);
      }
    })();
  }

  // ADMIN: Publications moderation page
  const adminPubsTable = document.getElementById('adminPubs');
  if (adminPubsTable) {
    (async () => {
      try {
        const res = await fetch('/api/admin/publications?status=pending');
        const list = await res.json().catch(() => []);
        adminPubsTable.innerHTML = Array.isArray(list) ? list.map(p => `
          <tr>
            <td>${p.title || ''}</td>
            <td>${p.author || ''}</td>
            <td>${p.domain || ''}</td>
            <td>
              <button class="btn small" data-approve="${p._id || p.id}">Approve</button>
              <button class="btn small danger" data-reject="${p._id || p.id}">Reject</button>
            </td>
          </tr>
        `).join('') : '';

        adminPubsTable.addEventListener('click', async (ev) => {
          const approve = ev.target.closest('[data-approve]');
          const reject = ev.target.closest('[data-reject]');
          if (approve) {
            const id = approve.getAttribute('data-approve');
            const resp = await fetch(`/api/admin/publications/${encodeURIComponent(id)}/approve`, { method: 'POST' });
            if (resp.ok) { notify('Approved'); approve.closest('tr').remove(); }
            else notify('Failed to approve', true);
          }
          if (reject) {
            const id = reject.getAttribute('data-reject');
            const note = prompt('Reason (optional):') || '';
            const resp = await fetch(`/api/admin/publications/${encodeURIComponent(id)}/reject`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ evaluationNote: note }) });
            if (resp.ok) { notify('Rejected'); reject.closest('tr').remove(); }
            else notify('Failed to reject', true);
          }
        });
      } catch (err) {
        notify('Failed to load pending publications: ' + err.message, true);
      }
    })();
  }

  // ADMIN: Domains page CRUD
  const domainsTable = document.getElementById('domainsTable');
  const domainForm = document.getElementById('domainForm');
  if (domainsTable) {
    (async function loadDomains() {
      try {
        const res = await fetch('/api/admin/domains');
        const list = await res.json().catch(() => []);
        domainsTable.innerHTML = Array.isArray(list) ? list.map(d => `
          <tr>
            <td>${d.name || ''}</td>
            <td>${d.description || ''}</td>
            <td>
              <button class="btn small" data-edit='${JSON.stringify(d).replace(/'/g, "&#39;")}'>Edit</button>
              <button class="btn small danger" data-del='${d._id || d.id}'>Delete</button>
            </td>
          </tr>
        `).join('') : '';
      } catch (err) {
        notify('Failed to load domains: ' + err.message, true);
      }

      domainsTable.addEventListener('click', async (ev) => {
        const del = ev.target.closest('[data-del]');
        const edit = ev.target.closest('[data-edit]');
        if (del) {
          const id = del.getAttribute('data-del');
          if (!confirm('Delete this domain?')) return;
          const resp = await fetch(`/api/admin/domains/${encodeURIComponent(id)}`, { method: 'DELETE' });
          if (resp.ok) { notify('Deleted'); del.closest('tr').remove(); }
          else notify('Failed to delete domain', true);
        }
        if (edit) {
          try {
            const d = JSON.parse(edit.getAttribute('data-edit'));
            if (domainForm) {
              domainForm.querySelector('[name="id"]').value = d._id || d.id || '';
              domainForm.querySelector('[name="name"]').value = d.name || '';
              domainForm.querySelector('[name="description"]').value = d.description || '';
            }
          } catch {}
        }
      });
    })();

    if (domainForm) {
      domainForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const fd = new FormData(domainForm);
        const id = fd.get('id');
        const payload = { name: fd.get('name'), description: fd.get('description') };
        try {
          let resp;
          if (id) {
            resp = await fetch(`/api/admin/domains/${encodeURIComponent(id)}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
          } else {
            resp = await fetch(`/api/admin/domains`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
          }
          if (resp.ok) { notify('Saved'); window.location.reload(); }
          else notify('Failed to save domain', true);
        } catch (err) { notify('Failed to save domain: ' + err.message, true); }
      });
    }
  }

  // SEARCH PAGE: form submit -> fetch -> render
  const searchForm = document.getElementById('searchForm');
  const searchResults = document.getElementById('searchResults');
  const searchEmpty = document.getElementById('searchEmpty');
  if (searchForm && searchResults) {
    searchForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const data = Object.fromEntries(new FormData(searchForm));
      const params = new URLSearchParams();
      if (data.domain) params.set('domain', data.domain);
      if (data.q) params.set('q', data.q);
      if (data.author) params.set('author', data.author);
      if (data.from) params.set('from', data.from);
      if (data.to) params.set('to', data.to);
      try {
        const res = await fetch(`/api/search?${params.toString()}`);
        const list = await res.json().catch(() => []);
        searchResults.innerHTML = Array.isArray(list) && list.length > 0 ? list.map(p => `
          <div class="card">
            <div class="card-body">
              <h3>${p.title || ''}</h3>
              <p class="muted">${p.domain || ''}</p>
              <p>${p.description || ''}</p>
              <div class="actions">
                ${p.pdfFileName ? `<a class="btn small" href="/pdfs/${p.pdfFileName}" target="_blank">Download PDF</a>` : ''}
                <a class="btn small" href="/publications/${p._id || p.id}">View</a>
              </div>
            </div>
          </div>
        `).join('') : '';
        if (searchEmpty) searchEmpty.style.display = (!Array.isArray(list) || list.length === 0) ? '' : 'none';
      } catch (err) {
        if (searchEmpty) searchEmpty.textContent = 'Search failed: ' + err.message;
        if (searchEmpty) searchEmpty.style.display = '';
      }
    });
  }

  // HEADER LOGIN/LOGOUT TOGGLE
  const publicationsLink = document.getElementById('publicationsLink');
  const searchLink = document.getElementById('searchLink');
  const submitLink = document.getElementById('submitLink');
  const myPubsLink = document.getElementById('myPubsLink');
  const profileLink = document.getElementById('profileLink');
  const adminLink = document.getElementById('adminLink');
  const loginLink = document.getElementById('loginLink');
  const registerLink = document.getElementById('registerLink');
  const logoutLink = document.getElementById('logoutLink');
  try {
    const currentUser = (() => { try { return JSON.parse(localStorage.getItem('currentUser')); } catch { return null; } })();
    const loggedIn = !!(currentUser && (currentUser._id || currentUser.id));
    const isAdmin = loggedIn && currentUser.usertype === 'admin';
    // Default all hidden then selectively show
    function show(el, flag) { if (el) el.style.display = flag ? '' : 'none'; }

    // Publications always visible
    show(publicationsLink, true);

    if (!loggedIn) {
      // Logged out: Publications, Login, Register only
      show(searchLink, false);
      show(submitLink, false);
      show(myPubsLink, false);
      show(profileLink, false);
      show(adminLink, false);
      show(loginLink, true);
      show(registerLink, true);
      show(logoutLink, false);
    } else if (isAdmin) {
      // Admin: Publications, Admin, Logout only
      show(searchLink, false);
      show(submitLink, false);
      show(myPubsLink, false);
      show(profileLink, false);
      show(adminLink, true);
      show(loginLink, false);
      show(registerLink, false);
      show(logoutLink, true);
    } else {
      // Normal user: Publications, Search, Submit, My Publications, Profile, Logout
      show(searchLink, true);
      show(submitLink, true);
      show(myPubsLink, true);
      show(profileLink, true);
      show(adminLink, false);
      show(loginLink, false);
      show(registerLink, false);
      show(logoutLink, true);
    }
    if (logoutLink) {
      logoutLink.addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.removeItem('currentUser');
        notify('Logged out');
        window.location.href = '/';
      });
    }

    // Show status badges on publications only when logged in
    const publicationsGrid = document.querySelector('main .grid');
    if (publicationsGrid) {
      publicationsGrid.querySelectorAll('.badge').forEach(b => {
        b.style.display = loggedIn ? '' : 'none';
      });
    }
  } catch (_) { /* ignore */ }

  // Publications page filtering: show only accepted to non-admin users
  (function filterPublicationsForRole() {
    const grid = document.querySelector('main .grid');
    if (!grid) return;
    const cards = Array.from(grid.querySelectorAll('.card[data-status]'));
    if (cards.length === 0) return;
    let user = null;
    try { user = JSON.parse(localStorage.getItem('currentUser') || 'null'); } catch {}
    const isAdmin = !!(user && (user._id || user.id) && user.usertype === 'admin');
    if (isAdmin) return; // admins see all
    let anyVisible = false;
    cards.forEach(card => {
      const status = (card.getAttribute('data-status') || '').toLowerCase();
      if (status === 'accepted') {
        card.style.display = '';
        anyVisible = true;
      } else {
        card.style.display = 'none';
      }
    });
    if (!anyVisible) {
      const p = document.createElement('p');
      p.className = 'muted';
      p.textContent = 'No accepted publications available yet.';
      grid.after(p);
    }
  })();

  // Index hero buttons configuration
  const heroPrimaryBtn = document.getElementById('heroPrimaryBtn');
  const heroSecondaryBtn = document.getElementById('heroSecondaryBtn');
  try {
    const u = JSON.parse(localStorage.getItem('currentUser') || 'null');
    const loggedIn = !!(u && (u._id || u.id));
    const isAdmin = loggedIn && u.usertype === 'admin';
    if (heroPrimaryBtn && heroSecondaryBtn) {
      if (!loggedIn) {
        heroPrimaryBtn.textContent = 'Browse Publications';
        heroPrimaryBtn.setAttribute('href', '/publications');
        heroSecondaryBtn.textContent = 'Login';
        heroSecondaryBtn.setAttribute('href', '/login');
      } else if (isAdmin) {
        heroPrimaryBtn.textContent = 'Go to Admin';
        heroPrimaryBtn.setAttribute('href', '/admin');
        heroSecondaryBtn.textContent = 'View Publications';
        heroSecondaryBtn.setAttribute('href', '/publications');
      } else {
        heroPrimaryBtn.textContent = 'Submit Publication';
        heroPrimaryBtn.setAttribute('href', '/submit');
        heroSecondaryBtn.textContent = 'Search';
        heroSecondaryBtn.setAttribute('href', '/search');
      }
    }
  } catch (_) { /* ignore */ }

  // LOGIN
  const loginForm = document.getElementById('loginForm');
  if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const data = Object.fromEntries(new FormData(loginForm));
      try {
        const res = await fetch(base + '/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email: data.email, password: data.password })
        });
        const maybeJson = await res.text();
        let user = null;
        try { user = maybeJson ? JSON.parse(maybeJson) : null; } catch (_) { user = null; }
        if (res.ok && user && (user._id || user.id)) {
          notify('Login successful');
          // store minimal session info
          localStorage.setItem('currentUser', JSON.stringify(user));
          window.location.href = '/publications';
        } else {
          const msg = (user && user.message) ? user.message : (maybeJson || 'Invalid email or password');
          notify(msg, true);
        }
      } catch (err) {
        notify('Login failed: ' + err.message, true);
      }
    });
  }

  // PROFILE PAGE: load + save
  const profileForm = document.getElementById('profileForm');
  if (profileForm) {
    (async () => {
      let user = null;
      try { user = JSON.parse(localStorage.getItem('currentUser') || 'null'); } catch {}
      if (!user || !(user._id || user.id)) {
        notify('Please login to view profile', true);
        window.location.href = '/login';
        return;
      }
      const userId = user._id || user.id;
      try {
        const res = await fetch(`/api/me?userId=${encodeURIComponent(userId)}`);
        const body = await res.json().catch(() => null);
        const u = body || user;
        profileForm.querySelector('[name="username"]').value = u.username || '';
        profileForm.querySelector('[name="email"]').value = u.email || '';
        profileForm.querySelector('[name="about"]').value = u.about || '';
        profileForm.querySelector('[name="avatarUrl"]').value = u.avatarUrl || '';
        profileForm.querySelector('[name="domain"]').value = u.domain || '';
        profileForm.querySelector('[name="qualification"]').value = u.qualification || '';
      } catch {}

      profileForm.addEventListener('submit', async (ev) => {
        ev.preventDefault();
        const data = Object.fromEntries(new FormData(profileForm));
        try {
          const res = await fetch(`/api/me?userId=${encodeURIComponent(userId)}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              username: data.username,
              email: data.email,
              about: data.about,
              avatarUrl: data.avatarUrl,
              domain: data.domain,
              qualification: data.qualification
            })
          });
          const updated = await res.json().catch(() => null);
          if (res.ok && updated) {
            localStorage.setItem('currentUser', JSON.stringify(updated));
            notify('Profile updated');
          } else {
            notify('Failed to update profile', true);
          }
        } catch (err) {
          notify('Failed to update profile: ' + err.message, true);
        }
      });
    })();
  }

  // MY PUBLICATIONS PAGE: list and delete
  const myPubsContainer = document.getElementById('myPublications');
  if (myPubsContainer) {
    (async () => {
      let user = null;
      try { user = JSON.parse(localStorage.getItem('currentUser') || 'null'); } catch {}
      if (!user || !(user._id || user.id)) {
        notify('Please login to view your publications', true);
        window.location.href = '/login';
        return;
      }
      const ownerId = user._id || user.id;
      try {
        const res = await fetch(`/api/my-publications?ownerId=${encodeURIComponent(ownerId)}`);
        const list = await res.json().catch(() => []);
        if (!Array.isArray(list)) return;
        myPubsContainer.innerHTML = list.map(p => `
          <div class="card">
            <div class="card-body">
              <h3>${p.title || ''}</h3>
              <p class="muted">${p.domain || ''}</p>
              <p>${p.description || ''}</p>
              <div class="actions">
                <a class="btn small" href="/publications/${p._id || p.id}">View</a>
                <button class="btn small danger" data-del="${p._id || p.id}">Delete</button>
              </div>
            </div>
          </div>
        `).join('');

        myPubsContainer.addEventListener('click', async (ev) => {
          const btn = ev.target.closest('[data-del]');
          if (!btn) return;
          const pubId = btn.getAttribute('data-del');
          if (!confirm('Delete this publication?')) return;
          try {
            const resp = await fetch(`/api/publications/${encodeURIComponent(pubId)}?ownerId=${encodeURIComponent(ownerId)}`, { method: 'DELETE' });
            if (resp.ok) {
              notify('Deleted');
              btn.closest('.card').remove();
            } else {
              const txt = await resp.text().catch(() => '');
              notify('Failed to delete: ' + txt, true);
            }
          } catch (err) {
            notify('Failed to delete: ' + err.message, true);
          }
        });
      } catch (err) {
        notify('Failed to load your publications: ' + err.message, true);
      }
    })();
  }

  // REGISTER
  const registerForm = document.getElementById('registerForm');
  if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const data = Object.fromEntries(new FormData(registerForm));
      try {
        // backend expects: username, email, password (+ optional usertype, domain, qualification)
        const payload = {
          username: data.name || data.username || '',
          email: data.email || '',
          password: data.password || '',
          usertype: data.usertype || 'author',
          domain: data.domain || '',
          qualification: data.qualification || ''
        };
        const res = await fetch(base + '/register', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
        const raw = await res.text();
        let body = null; try { body = raw ? JSON.parse(raw) : null; } catch (_) { body = null; }

        if (res.status === 201) {
          notify('Registration successful. You can login now.');
          window.location.href = '/login';
          return;
        }

        if (res.status === 409) {
          notify('Email already registered', true);
          return;
        }

        if (res.status === 400) {
          const msg = (typeof body === 'string') ? body : (body && body.message) || 'Invalid input';
          notify(msg, true);
          return;
        }

        const fallback = (typeof body === 'string') ? body : (body && body.message) || raw || 'Registration failed';
        notify(fallback, true);
      } catch (err) {
        notify('Registration failed: ' + err.message, true);
      }
    });
  }

  // SUBMIT PUBLICATION
  const submitForm = document.getElementById('submitForm');
  if (submitForm) {
    submitForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const fd = new FormData(submitForm);

      // keywords: convert comma separated to multiple params expected by backend
      const rawKeywords = (fd.get('keywords') || '').toString();
      fd.delete('keywords');
      rawKeywords.split(',').map(k => k.trim()).filter(Boolean).forEach(k => fd.append('keywords', k));

      // inject author + authorId from current user
      try {
        const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null');
        if (currentUser) {
          if (!fd.get('author')) fd.append('author', currentUser.username || currentUser.email || '');
          if (!fd.get('authorId')) fd.append('authorId', currentUser._id || currentUser.id || '');
        }
      } catch (_) {}

      try {
        const res = await fetch(base + '/new-publication', {
          method: 'POST',
          body: fd
        });
        if (res.ok) {
          notify('Publication submitted successfully.');
          window.location.href = '/publications';
        } else {
          const txt = await res.text().catch(() => '');
          notify('Failed to submit publication: ' + txt, true);
        }
      } catch (err) {
        notify('Failed to submit publication: ' + err.message, true);
      }
    });
  }
})();
