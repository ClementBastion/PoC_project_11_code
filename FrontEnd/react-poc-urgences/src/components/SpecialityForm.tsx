import React, { useState, useEffect } from 'react';
import { fetchRecommendedHospital } from '../api/hospitals';
import SpecialitySelect from './SpecialitySelect';
import LocationInput from './LocationInput';
import SubmitButton from './SubmitButton';
import type { Coordinates, Speciality } from "../types.tsx";
import axiosWithAuth from "../api/axiosWithAuth.tsx";
import { Box, Paper, Alert } from '@mui/material';

interface Props {
    onResult: (result: any) => void;
}

const SpecialityForm: React.FC<Props> = ({ onResult }) => {
    const [specialities, setSpecialities] = useState<Speciality[]>([]);
    const [speciality, setSelectedSpeciality] = useState<Speciality | null>(null);
    const [location, setLocation] = useState<Coordinates>({ lat: 0, lon: 0 });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

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

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const result = await fetchRecommendedHospital(location.lat, location.lon, speciality!.id);
            onResult(result);
        } catch {
            setError('Error fetching hospital recommendation.');
        } finally {
            setLoading(false);
        }
    };

    const handleSpecialityChange = (id: number) => {
        const found = specialities.find((s) => s.id === id) || null;
        setSelectedSpeciality(found);
    };

    return (
        <Paper
            elevation={3}
            sx={{
                p: 3,
                mt: 3,
                mb: 2,
                borderRadius: 2,
            }}
        >
            <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <SpecialitySelect
                    specialities={specialities}
                    selected={speciality}
                    onChange={handleSpecialityChange}
                />
                <LocationInput value={location} onChange={setLocation} />
                <SubmitButton loading={loading} />
                {error && <Alert severity="error">{error}</Alert>}
            </Box>
        </Paper>
    );
};

export default SpecialityForm;
