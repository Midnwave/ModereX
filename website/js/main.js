// ============================================
// ModereX Landing Page - Enhanced JavaScript
// ============================================

document.addEventListener('DOMContentLoaded', () => {
    initNavbar();
    initScrollAnimations();
    initMobileMenu();
    initSmoothScroll();
    fetchLiveStats();
    initTypingAnimation();
    initCountUpAnimations();
    initButtonEffects();
    initPanelMockAnimations();
});

// ============================================
// Navbar Enhancement
// ============================================

function initNavbar() {
    const navbar = document.querySelector('.navbar');
    if (!navbar) return;

    window.addEventListener('scroll', () => {
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });
}

// ============================================
// Mobile Menu
// ============================================

function initMobileMenu() {
    const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
    const navLinks = document.querySelector('.nav-links');

    if (!mobileMenuBtn || !navLinks) return;

    mobileMenuBtn.addEventListener('click', () => {
        navLinks.classList.toggle('mobile-open');
        const icon = mobileMenuBtn.querySelector('i');

        if (navLinks.classList.contains('mobile-open')) {
            icon.classList.remove('fa-bars');
            icon.classList.add('fa-times');
        } else {
            icon.classList.remove('fa-times');
            icon.classList.add('fa-bars');
        }
    });

    // Close menu when clicking a link
    navLinks.querySelectorAll('a').forEach(link => {
        link.addEventListener('click', () => {
            navLinks.classList.remove('mobile-open');
            const icon = mobileMenuBtn.querySelector('i');
            icon.classList.remove('fa-times');
            icon.classList.add('fa-bars');
        });
    });

    // Close menu when clicking outside
    document.addEventListener('click', (e) => {
        if (!navLinks.contains(e.target) && !mobileMenuBtn.contains(e.target)) {
            navLinks.classList.remove('mobile-open');
            const icon = mobileMenuBtn.querySelector('i');
            icon.classList.remove('fa-times');
            icon.classList.add('fa-bars');
        }
    });
}

// ============================================
// Smooth Scroll
// ============================================

function initSmoothScroll() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const targetId = this.getAttribute('href');

            if (targetId === '#') return;

            const target = document.querySelector(targetId);
            if (!target) return;

            const navbar = document.querySelector('.navbar');
            const navHeight = navbar ? navbar.offsetHeight : 0;
            const targetPosition = target.getBoundingClientRect().top + window.pageYOffset - navHeight - 20;

            window.scrollTo({
                top: targetPosition,
                behavior: 'smooth'
            });
        });
    });

    // Scroll indicator in hero
    const scrollIndicator = document.querySelector('.scroll-indicator');
    if (scrollIndicator) {
        scrollIndicator.addEventListener('click', () => {
            const featuresSection = document.querySelector('#features');
            if (featuresSection) {
                const navbar = document.querySelector('.navbar');
                const navHeight = navbar ? navbar.offsetHeight : 0;
                const targetPosition = featuresSection.getBoundingClientRect().top + window.pageYOffset - navHeight;

                window.scrollTo({
                    top: targetPosition,
                    behavior: 'smooth'
                });
            }
        });
    }
}

// ============================================
// Scroll Animations
// ============================================

function initScrollAnimations() {
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -100px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry, index) => {
            if (entry.isIntersecting) {
                // Add stagger delay for feature cards
                if (entry.target.classList.contains('feature-card') ||
                    entry.target.classList.contains('comparison-card')) {
                    const cards = entry.target.parentElement.children;
                    const cardIndex = Array.from(cards).indexOf(entry.target);
                    entry.target.style.transitionDelay = `${cardIndex * 0.1}s`;
                }

                entry.target.classList.add('animate-in');

                // Trigger count-up animation for stat values
                if (entry.target.classList.contains('stat')) {
                    const valueElement = entry.target.querySelector('.stat-value.count-up');
                    if (valueElement && !valueElement.classList.contains('counted')) {
                        valueElement.classList.add('counted');
                        animateValue(valueElement, valueElement.dataset.target);
                    }
                }
            }
        });
    }, observerOptions);

    // Observe elements for scroll animations
    const elementsToAnimate = document.querySelectorAll(`
        .feature-card,
        .panel-window,
        .panel-feature,
        .comparison-card,
        .download-card,
        .stat,
        .gateway-text,
        .gateway-demo,
        .section-header
    `);

    elementsToAnimate.forEach(el => {
        observer.observe(el);
    });

    // Add animation styles
    addScrollAnimationStyles();
}

function addScrollAnimationStyles() {
    const style = document.createElement('style');
    style.textContent = `
        .feature-card,
        .panel-window,
        .panel-feature,
        .comparison-card,
        .download-card,
        .stat,
        .gateway-text,
        .gateway-demo,
        .section-header {
            opacity: 0;
            transform: translateY(30px);
            transition: opacity 0.6s ease, transform 0.6s ease;
        }

        .feature-card.animate-in,
        .panel-window.animate-in,
        .panel-feature.animate-in,
        .comparison-card.animate-in,
        .download-card.animate-in,
        .stat.animate-in,
        .gateway-text.animate-in,
        .gateway-demo.animate-in,
        .section-header.animate-in {
            opacity: 1;
            transform: translateY(0);
        }

        .gateway-text {
            transform: translateX(-30px);
        }

        .gateway-text.animate-in {
            transform: translateX(0);
        }

        .gateway-demo {
            transform: translateX(30px);
        }

        .gateway-demo.animate-in {
            transform: translateX(0);
        }
    `;
    document.head.appendChild(style);
}

// ============================================
// Fetch Live Stats from Gateway
// ============================================

async function fetchLiveStats() {
    const statsElement = document.getElementById('liveServers');
    if (!statsElement) return;

    try {
        // Try quick tunnel URL first (current setup)
        let gatewayUrl = 'https://neighbors-steps-unable-stop.trycloudflare.com/api/stats';

        const response = await fetch(gatewayUrl, {
            method: 'GET',
            headers: { 'Accept': 'application/json' },
            signal: AbortSignal.timeout(5000)
        });

        if (response.ok) {
            const data = await response.json();
            const serverCount = data.servers || 0;

            // Update the data-target attribute for count-up animation
            statsElement.dataset.target = serverCount;

            // If already visible, animate immediately
            if (statsElement.closest('.stat').classList.contains('animate-in')) {
                animateValue(statsElement, serverCount);
            }
        } else {
            statsElement.textContent = '--';
        }
    } catch (error) {
        console.log('[Stats] Gateway not available yet');
        statsElement.textContent = '--';
        statsElement.dataset.target = '0';
    }

    // Refresh every 30 seconds
    setTimeout(fetchLiveStats, 30000);
}

// ============================================
// Count-Up Animation
// ============================================

function initCountUpAnimations() {
    // Initialize count-up elements
    document.querySelectorAll('.count-up').forEach(el => {
        if (!el.dataset.target) {
            const currentText = el.textContent.trim();
            // Try to extract number from text like "10+" or "100%"
            const match = currentText.match(/\d+/);
            if (match) {
                el.dataset.target = match[0];
            } else {
                el.dataset.target = '0';
            }
        }
    });
}

function animateValue(element, target) {
    const targetValue = parseInt(target) || 0;
    const duration = 2000;
    const startValue = 0;
    const startTime = performance.now();

    function update(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        const eased = easeOutCubic(progress);
        const current = Math.round(startValue + (targetValue - startValue) * eased);

        element.textContent = current.toLocaleString();

        if (progress < 1) {
            requestAnimationFrame(update);
        } else {
            // Add suffix if original had one
            const originalText = element.textContent;
            if (element.classList.contains('has-suffix')) {
                element.textContent = current.toLocaleString() + '+';
            }
        }
    }

    requestAnimationFrame(update);
}

function easeOutCubic(t) {
    return 1 - Math.pow(1 - t, 3);
}

// ============================================
// Typing Animation for Gateway URL
// ============================================

function initTypingAnimation() {
    const typingElement = document.querySelector('.typing-animation');
    if (!typingElement) return;

    const text = typingElement.textContent;
    typingElement.textContent = '';
    typingElement.style.display = 'inline-block';
    typingElement.style.borderRight = '2px solid var(--primary-light)';
    typingElement.style.paddingRight = '2px';

    let index = 0;
    const typingSpeed = 150;

    function type() {
        if (index < text.length) {
            typingElement.textContent += text.charAt(index);
            index++;
            setTimeout(type, typingSpeed);
        } else {
            // Blinking cursor effect
            setInterval(() => {
                typingElement.style.borderRightColor =
                    typingElement.style.borderRightColor === 'transparent'
                        ? 'var(--primary-light)'
                        : 'transparent';
            }, 500);
        }
    }

    // Start typing when element comes into view
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting && index === 0) {
                setTimeout(type, 500);
                observer.disconnect();
            }
        });
    }, { threshold: 0.5 });

    observer.observe(typingElement);
}

// ============================================
// Parallax Effect for Hero Background
// ============================================

window.addEventListener('scroll', () => {
    const scrolled = window.pageYOffset;
    const heroGlows = document.querySelectorAll('.hero-glow');

    heroGlows.forEach((glow, index) => {
        const speed = 0.3 + (index * 0.1);
        glow.style.transform = `translate(-50%, ${scrolled * speed}px)`;
    });
});

// ============================================
// Add Particle Effect to Hero
// ============================================

function createParticles() {
    const particlesContainer = document.querySelector('.floating-particles');
    if (!particlesContainer) return;

    const particleCount = 30;

    for (let i = 0; i < particleCount; i++) {
        const particle = document.createElement('div');
        particle.className = 'particle';
        particle.style.cssText = `
            position: absolute;
            width: ${Math.random() * 4 + 1}px;
            height: ${Math.random() * 4 + 1}px;
            background: hsla(215, 85%, 55%, ${Math.random() * 0.5 + 0.1});
            border-radius: 50%;
            left: ${Math.random() * 100}%;
            top: ${Math.random() * 100}%;
            animation: float ${Math.random() * 10 + 10}s ease-in-out infinite;
            animation-delay: ${Math.random() * 5}s;
            filter: blur(${Math.random() * 2}px);
        `;
        particlesContainer.appendChild(particle);
    }
}

// Initialize particles after a short delay
setTimeout(createParticles, 1000);

// ============================================
// Easter Egg: Konami Code
// ============================================

let konamiCode = [];
const konamiSequence = ['ArrowUp', 'ArrowUp', 'ArrowDown', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'ArrowLeft', 'ArrowRight', 'b', 'a'];

document.addEventListener('keydown', (e) => {
    konamiCode.push(e.key);
    konamiCode = konamiCode.slice(-konamiSequence.length);

    if (konamiCode.join('') === konamiSequence.join('')) {
        activateEasterEgg();
    }
});

function activateEasterEgg() {
    console.log('%c🎉 Konami Code Activated! 🎉', 'font-size: 24px; color: #2d7aed; font-weight: bold;');
    console.log('%cYou found the secret! Join our Discord for more surprises:', 'font-size: 16px; color: #8b5cf6;');
    console.log('%chttps://discord.gg/jQGMhKA5m6', 'font-size: 16px; color: #22c55e; font-weight: bold;');

    // Add rainbow animation to logo
    const logo = document.querySelector('.logo-icon');
    if (logo) {
        logo.style.animation = 'rainbow 2s linear infinite';

        const style = document.createElement('style');
        style.textContent = `
            @keyframes rainbow {
                0% { filter: hue-rotate(0deg); }
                100% { filter: hue-rotate(360deg); }
            }
        `;
        document.head.appendChild(style);
    }
}

// ============================================
// Performance Monitoring
// ============================================

if ('PerformanceObserver' in window) {
    const perfObserver = new PerformanceObserver((list) => {
        for (const entry of list.getEntries()) {
            if (entry.entryType === 'largest-contentful-paint') {
                console.log(`LCP: ${entry.renderTime || entry.loadTime}ms`);
            }
        }
    });

    try {
        perfObserver.observe({ entryTypes: ['largest-contentful-paint'] });
    } catch (e) {
        // Browser doesn't support LCP
    }
}

// Log page load time
window.addEventListener('load', () => {
    setTimeout(() => {
        const perfData = performance.timing;
        const pageLoadTime = perfData.loadEventEnd - perfData.navigationStart;
        console.log(`%cPage loaded in ${pageLoadTime}ms`, 'color: #22c55e; font-weight: bold;');
    }, 0);
});

// ============================================
// Button Hover Effects with Bounce
// ============================================

function initButtonEffects() {
    const buttons = document.querySelectorAll('.btn');

    buttons.forEach(btn => {
        // Add magnetic effect on hover
        btn.addEventListener('mousemove', (e) => {
            const rect = btn.getBoundingClientRect();
            const x = e.clientX - rect.left - rect.width / 2;
            const y = e.clientY - rect.top - rect.height / 2;

            // Subtle magnetic pull effect
            btn.style.transform = `translate(${x * 0.1}px, ${y * 0.1}px) scale(1.05)`;
        });

        btn.addEventListener('mouseleave', () => {
            btn.style.transform = '';
        });

        // Add ripple effect on click
        btn.addEventListener('click', function(e) {
            const ripple = document.createElement('span');
            const rect = this.getBoundingClientRect();

            ripple.style.cssText = `
                position: absolute;
                border-radius: 50%;
                background: rgba(255, 255, 255, 0.3);
                width: 100px;
                height: 100px;
                left: ${e.clientX - rect.left - 50}px;
                top: ${e.clientY - rect.top - 50}px;
                transform: scale(0);
                animation: rippleEffect 0.6s ease-out;
                pointer-events: none;
            `;

            this.appendChild(ripple);
            setTimeout(() => ripple.remove(), 600);
        });
    });

    // Add ripple animation
    const style = document.createElement('style');
    style.textContent = `
        @keyframes rippleEffect {
            to {
                transform: scale(4);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(style);
}

// ============================================
// Panel Mock Animations
// ============================================

function initPanelMockAnimations() {
    const panelMock = document.querySelector('.panel-mock');
    if (!panelMock) return;

    // Animate stat values periodically
    const statValues = panelMock.querySelectorAll('.panel-stat-value');
    const originalValues = Array.from(statValues).map(el => parseInt(el.textContent) || 0);

    setInterval(() => {
        statValues.forEach((el, index) => {
            if (el.classList.contains('online')) {
                // Online players fluctuate
                const change = Math.floor(Math.random() * 5) - 2;
                const newValue = Math.max(15, Math.min(50, originalValues[index] + change));
                animateStatChange(el, parseInt(el.textContent), newValue);
                originalValues[index] = newValue;
            }
        });
    }, 5000);

    // Highlight nav items periodically
    const navItems = panelMock.querySelectorAll('.panel-nav-item');
    let currentActive = 0;

    setInterval(() => {
        navItems.forEach(item => item.classList.remove('active'));
        currentActive = (currentActive + 1) % navItems.length;
        navItems[currentActive].classList.add('active');

        // Update header title
        const headerTitle = panelMock.querySelector('.panel-header-title');
        if (headerTitle) {
            const titles = ['Dashboard', 'Players', 'Punishments', 'AutoMod', 'Activity', 'Settings'];
            headerTitle.textContent = titles[currentActive] || 'Dashboard';
        }
    }, 4000);

    // Add hover effects to table rows
    const tableRows = panelMock.querySelectorAll('.panel-table-row');
    tableRows.forEach(row => {
        row.addEventListener('mouseenter', () => {
            row.style.background = 'hsla(215, 85%, 55%, 0.1)';
        });
        row.addEventListener('mouseleave', () => {
            row.style.background = '';
        });
    });
}

function animateStatChange(element, from, to) {
    const duration = 500;
    const startTime = performance.now();

    function update(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        const current = Math.round(from + (to - from) * progress);
        element.textContent = current;

        if (progress < 1) {
            requestAnimationFrame(update);
        }
    }

    requestAnimationFrame(update);
}

// ============================================
// Coming Soon Modal
// ============================================

function showComingSoon(e) {
    if (e) e.preventDefault();
    const btn = e?.currentTarget;

    // Button press animation first
    if (btn) {
        btn.style.transform = 'scale(0.95)';
    }

    // After brief animation, show popup with slide-in
    setTimeout(() => {
        if (btn) {
            btn.style.transform = '';
        }

        const modal = document.getElementById('comingSoonModal');
        if (modal) {
            modal.classList.add('show');
        }
    }, 150);
}

function hideComingSoon() {
    const modal = document.getElementById('comingSoonModal');
    if (modal) {
        modal.classList.remove('show');
    }
}

// Close modal when clicking outside
document.addEventListener('click', (e) => {
    const modal = document.getElementById('comingSoonModal');
    if (modal && e.target === modal) {
        hideComingSoon();
    }
});

// Close modal on Escape key
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        hideComingSoon();
    }
});
