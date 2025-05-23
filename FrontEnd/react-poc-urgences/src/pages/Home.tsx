import React, { useState } from 'react';
import LoginForm from '../components/LoginForm';
import SpecialityForm from '../components/SpecialityForm';
import HospitalResult from '../components/HospitalResult';
import type {MinimalHospitalRecommendation} from "../types.tsx";

const App: React.FC = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('access_token'));
    const [hospital, setHospital] = useState<MinimalHospitalRecommendation | null>(null);

    const handleLoginSuccess = () => {
        setIsLoggedIn(true);
    };

    if (!isLoggedIn) {
        return <LoginForm onLoginSuccess={handleLoginSuccess} />;
    }

    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold mb-4">Emergency Hospital Recommendation</h1>
            <SpecialityForm onResult={setHospital} />
            {hospital && (
                <div className="mt-6">
                    <HospitalResult hospital={hospital} />
                </div>
            )}
        </div>
    );
};

export default App;
