// Toast Notification System
class Toast {
    constructor() {
        this.container = this.getContainer();
        this.toasts = [];
    }

    getContainer() {
        let container = document.querySelector('.toast-container');
        if (!container) {
            container = document.createElement('div');
            container.className = 'toast-container';
            document.body.appendChild(container);
        }
        return container;
    }

    show(message, type = 'info', duration = 3000, title = '') {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        
        const icon = this.getIcon(type);
        
        toast.innerHTML = `
            <div class="toast-icon">${icon}</div>
            <div class="toast-content">
                ${title ? `<div class="toast-title">${title}</div>` : ''}
                <p class="toast-message">${message}</p>
            </div>
            <button class="toast-close" onclick="this.parentElement.remove()">&times;</button>
        `;
        
        this.container.appendChild(toast);
        
        if (duration) {
            setTimeout(() => {
                toast.classList.add('hide');
                setTimeout(() => toast.remove(), 300);
            }, duration);
        }
        
        return toast;
    }

    success(message, duration = 3000, title = 'Success!') {
        return this.show(message, 'success', duration, title);
    }

    error(message, duration = 3000, title = 'Error!') {
        return this.show(message, 'error', duration, title);
    }

    warning(message, duration = 3000, title = 'Warning!') {
        return this.show(message, 'warning', duration, title);
    }

    info(message, duration = 3000, title = 'Info') {
        return this.show(message, 'info', duration, title);
    }

    getIcon(type) {
        const icons = {
            success: '<i class="fas fa-check-circle"></i>',
            error: '<i class="fas fa-times-circle"></i>',
            warning: '<i class="fas fa-exclamation-circle"></i>',
            info: '<i class="fas fa-info-circle"></i>',
            danger: '<i class="fas fa-times-circle"></i>'
        };
        return icons[type] || icons.info;
    }
}

// Create global toast instance
const toast = new Toast();

// Loading Overlay
class LoadingOverlay {
    static show(message = 'Loading...') {
        let overlay = document.querySelector('.loading-overlay');
        if (!overlay) {
            overlay = document.createElement('div');
            overlay.className = 'loading-overlay';
            overlay.innerHTML = `
                <div class="loading-content">
                    <div class="spinner primary"></div>
                    <p>${message}</p>
                </div>
            `;
            document.body.appendChild(overlay);
        }
        overlay.classList.remove('hidden');
        return overlay;
    }

    static hide() {
        const overlay = document.querySelector('.loading-overlay');
        if (overlay) {
            overlay.classList.add('hidden');
        }
    }

    static setMessage(message) {
        const content = document.querySelector('.loading-content p');
        if (content) {
            content.textContent = message;
        }
    }
}

// Confirmation Dialog
class ConfirmDialog {
    static show(title, message, onConfirm, onCancel) {
        let dialog = document.querySelector('.confirm-dialog');
        if (!dialog) {
            dialog = document.createElement('div');
            dialog.className = 'confirm-dialog';
            document.body.appendChild(dialog);
        }

        dialog.innerHTML = `
            <div class="confirm-box">
                <div class="confirm-header">
                    <h3 class="confirm-title">${title}</h3>
                </div>
                <div class="confirm-body">
                    ${message}
                </div>
                <div class="confirm-footer">
                    <button class="btn-cancel">Cancel</button>
                    <button class="btn-confirm">Confirm</button>
                </div>
            </div>
        `;

        dialog.classList.remove('hidden');

        const confirmBtn = dialog.querySelector('.btn-confirm');
        const cancelBtn = dialog.querySelector('.btn-cancel');

        confirmBtn.onclick = () => {
            dialog.classList.add('hidden');
            if (typeof onConfirm === 'function') {
                onConfirm();
            }
        };

        cancelBtn.onclick = () => {
            dialog.classList.add('hidden');
            if (typeof onCancel === 'function') {
                onCancel();
            }
        };

        // Close on background click
        dialog.onclick = (e) => {
            if (e.target === dialog) {
                dialog.classList.add('hidden');
                if (typeof onCancel === 'function') {
                    onCancel();
                }
            }
        };

        return dialog;
    }
}

// UI Utilities
class UIUtils {
    // Smooth scroll to element
    static scrollToElement(selector, offset = 100) {
        const element = document.querySelector(selector);
        if (element) {
            const top = element.getBoundingClientRect().top + window.pageYOffset - offset;
            window.scrollTo({
                top: top,
                behavior: 'smooth'
            });
        }
    }

    // Toggle active state on navigation
    static setActiveNav(selector) {
        document.querySelectorAll('.nav-links a').forEach(link => {
            link.classList.remove('active');
        });
        const activeLink = document.querySelector(selector);
        if (activeLink) {
            activeLink.classList.add('active');
        }
    }

    // Format currency
    static formatCurrency(value, currency = 'USD') {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: currency
        }).format(value);
    }

    // Format date
    static formatDate(date, locale = 'en-US') {
        return new Intl.DateTimeFormat(locale, {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        }).format(new Date(date));
    }

    // Format time
    static formatTime(date, locale = 'en-US') {
        return new Intl.DateTimeFormat(locale, {
            hour: '2-digit',
            minute: '2-digit'
        }).format(new Date(date));
    }

    // Check if element is in viewport
    static isInViewport(element) {
        const rect = element.getBoundingClientRect();
        return (
            rect.top >= 0 &&
            rect.left >= 0 &&
            rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
            rect.right <= (window.innerWidth || document.documentElement.clientWidth)
        );
    }

    // Add fade-in animation on scroll
    static observeElements(selector, callback) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('fade-in');
                    if (typeof callback === 'function') {
                        callback(entry.target);
                    }
                }
            });
        }, {
            threshold: 0.1
        });

        document.querySelectorAll(selector).forEach(el => {
            observer.observe(el);
        });
    }

    // Debounce function
    static debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // Form validation
    static validateEmail(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    }

    static validatePhone(phone) {
        const regex = /^\d{10,}$/;
        return regex.test(phone.replace(/\D/g, ''));
    }

    // Clone element animation
    static ripple(event) {
        const button = event.currentTarget;
        const circle = document.createElement('span');
        const diameter = Math.max(button.clientWidth, button.clientHeight);
        const radius = diameter / 2;

        circle.style.width = circle.style.height = diameter + 'px';
        circle.style.left = (event.clientX - button.offsetLeft - radius) + 'px';
        circle.style.top = (event.clientY - button.offsetTop - radius) + 'px';
        circle.classList.add('ripple');

        const ripple = button.querySelector('.ripple');
        if (ripple) {
            ripple.remove();
        }

        button.appendChild(circle);
    }

    // Table row highlight
    static highlightRow(selector) {
        document.querySelectorAll(selector).forEach(row => {
            row.addEventListener('click', function() {
                document.querySelectorAll(selector).forEach(r => r.classList.remove('highlighted'));
                this.classList.add('highlighted');
            });
        });
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    // Add smooth transitions
    UIUtils.observeElements('.card, .stat-card, .restaurant-card, .table-card', (el) => {
        el.style.animation = 'fadeIn 0.3s ease-out';
    });

    // Add ripple effect to buttons
    document.querySelectorAll('.btn, button').forEach(button => {
        button.addEventListener('click', UIUtils.ripple);
    });

    // Highlight table rows on click
    UIUtils.highlightRow('table tbody tr');
});

// Export for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { Toast, LoadingOverlay, ConfirmDialog, UIUtils };
}
