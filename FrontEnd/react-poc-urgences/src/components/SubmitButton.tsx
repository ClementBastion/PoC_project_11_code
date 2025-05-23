import React from 'react';
import { Button, CircularProgress } from '@mui/material';

interface Props {
    loading: boolean;
}

const SubmitButton: React.FC<Props> = ({ loading }) => (
    <Button
        type="submit"
        variant="contained"
        color="primary"
        disabled={loading}
        fullWidth
        sx={{ mt: 2, minHeight: 40 }}
        size="large"
    >
        {loading ? <CircularProgress size={24} color="inherit" /> : 'Find Hospital'}
    </Button>
);

export default SubmitButton;
