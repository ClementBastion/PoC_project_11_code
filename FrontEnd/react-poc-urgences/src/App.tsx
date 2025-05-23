import React, { useState } from 'react';
import { Container, Typography, Box, Paper } from '@mui/material';
import LoginForm from "./components/LoginForm";
import SpecialityForm from "./components/SpecialityForm";
import HospitalResult from "./components/HospitalResult";
import type {MinimalHospitalRecommendation} from "./types.tsx";

const App: React.FC = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('access_token'));
    const [hospital, setHospital] = useState<MinimalHospitalRecommendation | null>(null);

    const handleLoginSuccess = () => {
        setIsLoggedIn(true);
    };

    if (!isLoggedIn) {
        console.log('not logged in');
        return <LoginForm onLoginSuccess={handleLoginSuccess} />;
    }

    return (
        <Container maxWidth="sm" sx={{ mt: 6 }}>
            <Paper elevation={4} sx={{ p: 4 }}>
                <Typography variant="h4" fontWeight="bold" gutterBottom>
                    Emergency Hospital Recommendation
                </Typography>
                <SpecialityForm onResult={setHospital} />
                {hospital && (
                    <Box mt={6}>
                        <HospitalResult hospital={hospital} />
                    </Box>
                )}
            </Paper>
        </Container>
    );
};

export default App;
