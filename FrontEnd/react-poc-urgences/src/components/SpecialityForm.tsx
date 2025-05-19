import React, { useState, useEffect } from 'react';
import { fetchRecommendedHospital } from '../api/hospitals';
import SpecialitySelect from './SpecialitySelect';
import LocationInput from './LocationInput';
import SubmitButton from './SubmitButton';
import type {Coordinates, Speciality} from "../types.tsx";
import axiosWithAuth from "../api/axiosWithAuth.tsx";


interface Props {
    onResult: (result: any) => void;
}

// Main form that handles state and calls API on submit
const SpecialityForm: React.FC<Props> = ({ onResult }) => {
    const [specialities, setSpecialities] = useState<Speciality[]>([]);
    const [speciality, setSelectedSpeciality] = useState<Speciality | null>(null);
    const [location, setLocation] = useState<Coordinates>({ lat: 0, lon: 0 });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Load list of specialities from the backend on component mount
    useEffect(() => {
        const loadSpecialities = async () => {
            try {
                const BASE_URL = import.meta.env.VITE_API_BASE_URL;
                const res = await axiosWithAuth.get<Speciality[]>(`${BASE_URL}api/specialities`);
                setSpecialities(res.data);
            } catch {
                setError('Failed to load specialities.');
            }
        };

        loadSpecialities();
    }, []);

    // Handle form submission and call backend API
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const result = await fetchRecommendedHospital(location.lat,location.lon,speciality!.id);
            onResult(result);
        } catch {
            setError('Error fetching hospital recommendation.');
        } finally {
            setLoading(false);
        }
    };

    const handleSpecialityChange = (name: string) => {
        const found = specialities.find((s) => s.name === name) || null;
        setSelectedSpeciality(found);
    };

    return (
        <form onSubmit={handleSubmit} className="p-4 border rounded space-y-4">
            <SpecialitySelect
                specialities={specialities}
                selected={speciality}
                onChange={handleSpecialityChange}
            />
            <LocationInput value={location} onChange={setLocation} />
            <SubmitButton loading={loading} />
            {error && <p className="text-red-600">{error}</p>}
        </form>
    );
};

export default SpecialityForm;
