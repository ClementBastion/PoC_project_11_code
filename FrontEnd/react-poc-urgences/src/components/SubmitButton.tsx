import React from 'react';

interface Props {
    loading: boolean;
}

// Submit button with dynamic label depending on loading state
const SubmitButton: React.FC<Props> = ({ loading }) => (
    <button
        type="submit"
        className="bg-blue-600 text-white px-4 py-2 rounded disabled:opacity-50"
        disabled={loading}
    >
        {loading ? 'Searching...' : 'Find Hospital'}
    </button>
);

export default SubmitButton;
