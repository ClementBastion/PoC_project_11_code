import React from 'react';
import type { MinimalHospitalRecommendation } from "../types.tsx";
import { Card, CardContent, Typography, Box } from '@mui/material';

interface Props {
    hospital: MinimalHospitalRecommendation;
}

// Display hospital information
const HospitalResult: React.FC<Props> = ({ hospital }) => (
    <Card elevation={4} sx={{ borderRadius: 2, bgcolor: "#f9f9f9" }}>
        <CardContent>
            <Typography variant="h6" fontWeight="bold" gutterBottom>
                {hospital.name}
            </Typography>
            <Box sx={{ ml: 1 }}>
                <Typography variant="body1">
                    <strong>Latitude:</strong> {hospital.latitude}
                </Typography>
                <Typography variant="body1">
                    <strong>Longitude:</strong> {hospital.longitude}
                </Typography>
            </Box>
        </CardContent>
    </Card>
);

export default HospitalResult;
