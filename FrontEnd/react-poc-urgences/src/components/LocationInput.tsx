import React from 'react';
import type { Coordinates } from "../types.tsx";
import { Box, TextField } from '@mui/material';

interface Props {
    value: Coordinates;
    onChange: (value: Coordinates) => void;
}

const LocationInput: React.FC<Props> = ({ value, onChange }) => {
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value: inputValue } = e.target;
        const numValue = parseFloat(inputValue);

        onChange({
            ...value,
            [name]: isNaN(numValue) ? 0 : numValue,
        });
    };

    return (
        <Box display="flex" gap={2}>
            <TextField
                label="Latitude"
                type="number"
                name="lat"
                value={value.lat}
                onChange={handleChange}
                required
                fullWidth
                placeholder="e.g. 51.509865"
            />
            <TextField
                label="Longitude"
                type="number"
                name="lon"
                value={value.lon}
                onChange={handleChange}
                required
                fullWidth
                placeholder="e.g. -0.118092"
            />
        </Box>
    );
};

export default LocationInput;
