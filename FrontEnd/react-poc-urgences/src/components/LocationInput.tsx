import React from 'react';
import type {Coordinates} from "../types.tsx";

interface Props {
    value: Coordinates;
    onChange: (value: Coordinates) => void;
}

// Input component for entering latitude and longitude
const LocationInput: React.FC<Props> = ({ value, onChange }) => {
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value: inputValue } = e.target;
        const numValue = parseFloat(inputValue);

        if (!isNaN(numValue)) {
            onChange({
                ...value,
                [name]: numValue,
            });
        }
    };

    return (
        <div className="space-y-4">
            <div>
                <label className="block mb-1 font-semibold">Latitude:</label>
                <input
                    type="number"
                    name="lat"
                    value={value.lat}
                    onChange={handleChange}
                    required
                    step="any"
                    className="w-full p-2 border rounded"
                    placeholder="e.g. 51.509865"
                />
            </div>

            <div>
                <label className="block mb-1 font-semibold">Longitude:</label>
                <input
                    type="number"
                    name="lon"
                    value={value.lon}
                    onChange={handleChange}
                    required
                    step="any"
                    className="w-full p-2 border rounded"
                    placeholder="e.g. -0.118092"
                />
            </div>
        </div>
    );
};

export default LocationInput;
