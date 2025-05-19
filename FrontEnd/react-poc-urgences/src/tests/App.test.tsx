import { render, screen, fireEvent } from '@testing-library/react';
import App from "../App.tsx";

// Mock the subcomponents to isolate App behavior from their real implementations
vi.mock('../components/LoginForm', () => ({
    default: (props: any) => {
        // When rendered, this mock triggers onLoginSuccess when clicked
        return <div data-testid="login-form" onClick={props.onLoginSuccess}>LoginForm</div>;
    }
}));
vi.mock('../components/SpecialityForm', () => ({
    default: (props: any) =>
        // This mock triggers onResult with a fake hospital when clicked
        <div data-testid="speciality-form" onClick={() => props.onResult({ name: 'Test Hospital', latitude: 1, longitude: 2 })}>
            SpecialityForm
        </div>
}));
vi.mock('../components/HospitalResult', () => ({
    default: (props: any) =>
        // This mock simply displays the hospital name passed in props
        <div data-testid="hospital-result">{props.hospital?.name}</div>
}));

describe('App', () => {
    // Clear localStorage before each test to reset authentication state
    beforeEach(() => {
        localStorage.clear();
    });

    it('displays the LoginForm if not logged in', () => {
        render(<App />);
        expect(screen.getByTestId('login-form')).toBeInTheDocument();
        // The main heading should not be displayed when not logged in
        expect(screen.queryByText(/Emergency Hospital Recommendation/i)).not.toBeInTheDocument();
    });

    it('displays the SpecialityForm if logged in', () => {
        localStorage.setItem('access_token', 'dummy');
        render(<App />);
        expect(screen.getByTestId('speciality-form')).toBeInTheDocument();
        // The main heading should be present
        expect(screen.getByText(/Emergency Hospital Recommendation/i)).toBeInTheDocument();
        // LoginForm should not be visible
        expect(screen.queryByTestId('login-form')).not.toBeInTheDocument();
    });

    it('displays HospitalResult if a hospital is set', () => {
        localStorage.setItem('access_token', 'dummy');
        render(<App />);
        // Simulate result callback to set hospital in App state
        fireEvent.click(screen.getByTestId('speciality-form'));
        expect(screen.getByTestId('hospital-result')).toBeInTheDocument();
        expect(screen.getByText('Test Hospital')).toBeInTheDocument();
    });

    it('switches from LoginForm to SpecialityForm after login', () => {
        render(<App />);
        // Simulate successful login by clicking the mock LoginForm
        fireEvent.click(screen.getByTestId('login-form'));
        expect(screen.getByTestId('speciality-form')).toBeInTheDocument();
    });
});
