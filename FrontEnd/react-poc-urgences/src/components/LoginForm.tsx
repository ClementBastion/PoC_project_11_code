import React, { useState } from 'react';
import axios from 'axios';

const LoginForm: React.FC<{ onLoginSuccess: () => void }> = ({ onLoginSuccess }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<string | null>(null);

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        try {

            const res = await axios.post(
                `${import.meta.env.VITE_KEYCLOAK_URL}/realms/${import.meta.env.VITE_KEYCLOAK_REALM}/protocol/openid-connect/token`,
                new URLSearchParams({
                    grant_type: 'password',
                    client_id: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
                    username,
                    password,
                }),
                {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                }
            );

            const { access_token, refresh_token } = res.data;

            // Store tokens in localStorage
            localStorage.setItem('access_token', access_token);
            localStorage.setItem('refresh_token', refresh_token);

            onLoginSuccess();
        } catch {
            setError('Login failed. Please check your credentials.');
        }
    };

    return (
        <form onSubmit={handleLogin} className="p-4 border rounded space-y-4 max-w-sm mx-auto mt-8">
            <h2 className="text-xl font-bold mb-2">Login</h2>
            <div>
                <label className="block mb-1">Username:</label>
                <input
                    className="w-full p-2 border rounded"
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                />
            </div>
            <div>
                <label className="block mb-1">Password:</label>
                <input
                    className="w-full p-2 border rounded"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
            </div>
            <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded w-full">
                Login
            </button>
            {error && <p className="text-red-600 mt-2">{error}</p>}
        </form>
    );
};

export default LoginForm;
