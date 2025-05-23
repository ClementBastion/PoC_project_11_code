import axios from 'axios';

export async function refreshToken(): Promise<string | null> {
    const refresh_token = localStorage.getItem('refresh_token');

    if (!refresh_token) return null;

    try {
        const params = new URLSearchParams({
            grant_type: 'refresh_token',
            client_id: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
            refresh_token,
        });

        const res = await axios.post(
            `${import.meta.env.VITE_KEYCLOAK_URL}/realms/${import.meta.env.VITE_KEYCLOAK_REALM}/protocol/openid-connect/token`,
            params,
            {
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            }
        );

        // Save new tokens
        const { access_token, refresh_token: new_refresh } = res.data;
        localStorage.setItem('access_token', access_token);
        localStorage.setItem('refresh_token', new_refresh);

        return access_token;
    } catch (error) {
        console.warn('Failed to refresh token:', error);
        localStorage.clear();
        return null;
    }
}
