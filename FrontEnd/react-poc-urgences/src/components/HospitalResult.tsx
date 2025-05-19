import React from 'react';
import type {HospitalRecommendation} from "../types.tsx";

interface Props {
    hospital: HospitalRecommendation;
}

// Display hospital information
const HospitalResult: React.FC<Props> = ({ hospital }) => {
    return (
        <div className="border p-4 rounded shadow">
            <h2 className="text-xl font-bold mb-2">{hospital.name}</h2>
            <p><strong>Address:</strong> {hospital.address}</p>
            <p><strong>Available Beds:</strong> {hospital.availableBeds}</p>
            <p><strong>Distance:</strong> {hospital.distanceInKm.toFixed(1)} km</p>
            <p><strong>Specialities:</strong> {hospital.specialities.join(', ')}</p>
        </div>
    );
};

export default HospitalResult;
