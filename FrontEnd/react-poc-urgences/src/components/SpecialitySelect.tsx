import React from 'react';
import type { Speciality } from "../types.tsx";
import { FormControl, InputLabel, Select, MenuItem } from '@mui/material';

interface Props {
    specialities: Speciality[];
    selected: Speciality | null;
    onChange: (id: number) => void;
}

// Dropdown component to select a medical speciality
const SpecialitySelect: React.FC<Props> = ({ specialities, selected, onChange }) => (
    <FormControl fullWidth required>
        <InputLabel id="speciality-select-label">Medical Speciality</InputLabel>
        <Select
            labelId="speciality-select-label"
            value={selected?.id ?? ''}
            label="Medical Speciality"
            onChange={e => onChange(e.target.value)}
        >
            <MenuItem value="">-- Select --</MenuItem>
            {specialities.map((spec) => (
                <MenuItem key={spec.id} value={spec.id}>
                    {spec.name}
                </MenuItem>
            ))}
        </Select>
    </FormControl>
);

export default SpecialitySelect;
