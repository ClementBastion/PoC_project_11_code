import { render, screen, fireEvent, waitFor } from '@testing-library/react';

// Mock axios
vi.mock('axios');
import axios from 'axios';
import LoginForm from "../components/LoginForm.tsx";
import {vi} from "vitest";

describe('LoginForm', () => {
    const mockOnLoginSuccess = vi.fn();

    beforeEach(() => {
        // Clear mocks and localStorage before each test
        vi.clearAllMocks();
        localStorage.clear();
    });

    it('renders the login form fields and button', () => {
        render(<LoginForm onLoginSuccess={mockOnLoginSuccess} />);
        expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument();
    });

    it('allows typing in username and password fields', () => {
        render(<LoginForm onLoginSuccess={mockOnLoginSuccess} />);
        const userInput = screen.getByLabelText(/username/i) as HTMLInputElement;
        const passInput = screen.getByLabelText(/password/i) as HTMLInputElement;

        fireEvent.change(userInput, { target: { value: 'alice' } });
        fireEvent.change(passInput, { target: { value: 'secret' } });

        expect(userInput.value).toBe('alice');
        expect(passInput.value).toBe('secret');
    });

    it('calls onLoginSuccess and sets tokens on successful login', async () => {
        // Mock successful response from axios
        (axios.post as vi.Mock).mockResolvedValueOnce({
            data: {
                access_token: 'abc123',
                refresh_token: 'def456',
            }
        });

        render(<LoginForm onLoginSuccess={mockOnLoginSuccess} />);

        fireEvent.change(screen.getByLabelText(/username/i), { target: { value: 'alice' } });
        fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'secret' } });

        fireEvent.click(screen.getByRole('button', { name: /login/i }));

        // Wait for async code to run
        await waitFor(() => {
            expect(localStorage.getItem('access_token')).toBe('abc123');
            expect(localStorage.getItem('refresh_token')).toBe('def456');
            expect(mockOnLoginSuccess).toHaveBeenCalled();
        });
    });

    it('displays error message on login failure', async () => {
        // Mock failed response from axios
        (axios.post as vi.Mock).mockRejectedValueOnce(new Error('fail'));

        render(<LoginForm onLoginSuccess={mockOnLoginSuccess} />);

        fireEvent.change(screen.getByLabelText(/username/i), { target: { value: 'bob' } });
        fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'wrong' } });

        fireEvent.click(screen.getByRole('button', { name: /login/i }));

        // Wait for async code to run
        await waitFor(() => {
            expect(screen.getByText(/login failed/i)).toBeInTheDocument();
            expect(mockOnLoginSuccess).not.toHaveBeenCalled();
        });
    });
});
