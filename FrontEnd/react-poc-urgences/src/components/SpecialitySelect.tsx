import React from 'react';
import type {Speciality} from "../types.tsx";


interface Props {
    specialities: Speciality[];
    selected: Speciality | null;
    onChange: (value: string) => void;
}

// Dropdown component to select a medical speciality
const SpecialitySelect: React.FC<Props> = ({ specialities, selected, onChange }) => {
    return (
        <div>
            <label className="block mb-1 font-semibold">Medical speciality:</label>
            <select
                value={selected?.name ?? ''}
                onChange={(e) => onChange(e.target.value)}
                required
                className="w-full p-2 border rounded"
            >
                <option value="">-- Select --</option>
                {specialities.map((spec) => (
                    <option key={spec.id} value={spec.name}>
                        {spec.name}
                    </option>
                ))}
            </select>
        </div>
    );
};

export default SpecialitySelect;
