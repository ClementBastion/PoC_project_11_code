import { render, screen, fireEvent, waitFor } from '@testing-library/react';

// Mock subcomponents to avoid testing their internal behavior here
vi.mock('../components/SpecialitySelect', () => ({
    default: (props: any) => (
        <select
            data-testid="speciality-select"
            value={props.selected?.id || ''}
            onChange={e => props.onChange(Number(e.target.value))}
        >
            {props.specialities.map((spec: any) => (
                <option key={spec.id} value={spec.id}>{spec.name}</option>
            ))}
        </select>
    ),
}));
vi.mock('../components/LocationInput', () => ({
    default: (props: any) => (
        <div>
            <input
                data-testid="lat-input"
                value={props.value.lat}
                onChange={e => props.onChange({ ...props.value, lat: Number(e.target.value) })}
            />
            <input
                data-testid="lon-input"
                value={props.value.lon}
                onChange={e => props.onChange({ ...props.value, lon: Number(e.target.value) })}
            />
        </div>
    ),
}));
vi.mock('../components/SubmitButton', () => ({
    default: (props: any) => (
        <button data-testid="submit-btn" disabled={props.loading}>
            {props.loading ? "Searching..." : "Find Hospital"}
        </button>
    ),
}));

// Mock API calls
vi.mock('../api/axiosWithAuth.tsx', () => ({
    default: { get: vi.fn() },
}));
vi.mock('../api/hospitals', () => ({
    fetchRecommendedHospital: vi.fn(),
}));

import axiosWithAuth from '../api/axiosWithAuth.tsx';
import { fetchRecommendedHospital } from '../api/hospitals';
import SpecialityForm from "../components/SpecialityForm.tsx";

describe('SpecialityForm', () => {
    const fakeSpecialities = [
        { id: 1, name: 'Cardiology' },
        { id: 2, name: 'Immunology' },
    ];
    const fakeResult = { name: 'Hospital A', latitude: 10, longitude: 20 };
    const mockOnResult = vi.fn();

    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('renders the form and its fields', async () => {
        // Mock successful specialities fetch
        (axiosWithAuth.get as any).mockResolvedValueOnce({ data: fakeSpecialities });

        render(<SpecialityForm onResult={mockOnResult} />);
        // Wait for useEffect
        await waitFor(() => {
            expect(screen.getByTestId('speciality-select')).toBeInTheDocument();
        });
        expect(screen.getByTestId('lat-input')).toBeInTheDocument();
        expect(screen.getByTestId('lon-input')).toBeInTheDocument();
        expect(screen.getByTestId('submit-btn')).toBeInTheDocument();
    });

    it('displays an error if loading specialities fails', async () => {
        (axiosWithAuth.get as any).mockRejectedValueOnce(new Error('fail'));
        render(<SpecialityForm onResult={mockOnResult} />);
        await waitFor(() => {
            expect(screen.getByText(/failed to load specialities/i)).toBeInTheDocument();
        });
    });

    it('calls fetchRecommendedHospital and onResult on submit', async () => {
        (axiosWithAuth.get as any).mockResolvedValueOnce({ data: fakeSpecialities });
        (fetchRecommendedHospital as any).mockResolvedValueOnce(fakeResult);

        render(<SpecialityForm onResult={mockOnResult} />);

        // Wait for specialities to load
        await waitFor(() => {
            expect(screen.getByTestId('speciality-select')).toBeInTheDocument();
        });

        // Select a speciality and fill location
        fireEvent.change(screen.getByTestId('speciality-select'), { target: { value: 1 } });
        fireEvent.change(screen.getByTestId('lat-input'), { target: { value: '48.8' } });
        fireEvent.change(screen.getByTestId('lon-input'), { target: { value: '2.3' } });

        fireEvent.click(screen.getByTestId('submit-btn'));

        await waitFor(() => {
            expect(fetchRecommendedHospital).toHaveBeenCalledWith(48.8, 2.3, 1);
            expect(mockOnResult).toHaveBeenCalledWith(fakeResult);
        });
    });

    it('shows an error if fetchRecommendedHospital fails', async () => {
        (axiosWithAuth.get as any).mockResolvedValueOnce({ data: fakeSpecialities });
        (fetchRecommendedHospital as any).mockRejectedValueOnce(new Error('fail'));

        render(<SpecialityForm onResult={mockOnResult} />);

        await waitFor(() => {
            expect(screen.getByTestId('speciality-select')).toBeInTheDocument();
        });

        fireEvent.change(screen.getByTestId('speciality-select'), { target: { value: 2 } });
        fireEvent.click(screen.getByTestId('submit-btn'));

        await waitFor(() => {
            expect(screen.getByText(/error fetching hospital/i)).toBeInTheDocument();
            expect(mockOnResult).not.toHaveBeenCalled();
        });
    });
});
