// Toggle between login and register forms
const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');
const switchToRegister = document.getElementById('switchToRegister');
const switchToLogin = document.getElementById('switchToLogin');
const loginBtn = document.getElementById('loginBtn');
const registerBtn = document.getElementById('registerBtn');

function showLogin() {
    loginForm.classList.remove('hidden');
    registerForm.classList.add('hidden');
}

function showRegister() {
    loginForm.classList.add('hidden');
    registerForm.classList.remove('hidden');
}

switchToRegister.addEventListener('click', showRegister);
switchToLogin.addEventListener('click', showLogin);
loginBtn.addEventListener('click', showLogin);
registerBtn.addEventListener('click', showRegister);

// Toggle password visibility
document.querySelectorAll('[type="password"]').forEach(input => {
    const eyeBtn = input.nextElementSibling;
    if (eyeBtn && eyeBtn.tagName === 'BUTTON') {
        eyeBtn.addEventListener('click', () => {
            const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
            input.setAttribute('type', type);
            eyeBtn.innerHTML = type === 'password' ? '<i class="far fa-eye"></i>' : '<i class="far fa-eye-slash"></i>';
        });
    }
});

// Form submission
document.querySelectorAll('form').forEach(form => {
    form.addEventListener('submit', (e) => {
        e.preventDefault();
        // Here you would typically validate and submit the form
        alert('Form submitted! (This is a demo)');
    });
});
// DOM Elements
const verificationPopup = document.getElementById('verificationPopup');
const showVerificationBtn = document.getElementById('showVerificationBtn');
const closePopupBtn = document.getElementById('closePopupBtn');
const verifyBtn = document.getElementById('verifyBtn');
const countdownElement = document.getElementById('countdown');
const verificationInputs = document.querySelectorAll('.verification-input');

// Show popup
showVerificationBtn.addEventListener('click', () => {
    verificationPopup.classList.remove('hidden');
    startCountdown();
});

// Close popup
closePopupBtn.addEventListener('click', () => {
    verificationPopup.classList.add('hidden');
    resetVerification();
});

// Close when clicking outside
verificationPopup.addEventListener('click', (e) => {
    if (e.target === verificationPopup) {
        verificationPopup.classList.add('hidden');
        resetVerification();
    }
});

// Move to next input when a digit is entered
function moveToNext(input) {
    const index = parseInt(input.dataset.index);

    // Only allow numbers
    input.value = input.value.replace(/[^0-9]/g, '');

    if (input.value.length === 1 && index < 6) {
        document.querySelector(`.verification-input[data-index="${index + 1}"]`).focus();
    }

    checkVerificationCode();
}

// Check if all verification digits are filled
function checkVerificationCode() {
    let allFilled = true;
    let verificationCode = '';

    verificationInputs.forEach(input => {
        if (input.value.length === 0) {
            allFilled = false;
        }
        verificationCode += input.value;
    });

    verifyBtn.disabled = !allFilled;

    return verificationCode;
}

// Verify button click
verifyBtn.addEventListener('click', () => {
    const code = checkVerificationCode();

    // Here you would typically send the code to your backend for verification
    // For demo purposes, we'll just show an alert
    verifyBtn.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i> Verifying...';
    verifyBtn.disabled = true;

    setTimeout(() => {
        verifyBtn.innerHTML = '<i class="fas fa-check mr-2"></i> Verified!';

        // Simulate successful verification
        setTimeout(() => {
            alert(`Verification successful! Code: ${code}`);
            verificationPopup.classList.add('hidden');
            resetVerification();
        }, 800);
    }, 1500);
});




// Reset verification inputs
function resetVerification() {
    verificationInputs.forEach(input => {
        input.value = '';
    });
    verifyBtn.disabled = true;
    verifyBtn.innerHTML = '<span>Verify & Continue</span><i class="fas fa-arrow-right ml-2"></i>';
}

// Allow backspace to move to previous input
document.addEventListener('keydown', function (e) {
    if (e.key === 'Backspace') {
        const activeElement = document.activeElement;
        if (activeElement.classList.contains('verification-input') && activeElement.value.length === 0) {
            const index = parseInt(activeElement.dataset.index);
            if (index > 1) {
                document.querySelector(`.verification-input[data-index="${index - 1}"]`).focus();
            }
        }
    }
});
