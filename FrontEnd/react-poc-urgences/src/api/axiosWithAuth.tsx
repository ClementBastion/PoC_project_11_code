import axios from 'axios';
import { refreshToken } from '../auth/refreshToken';
import {isTokenExpired} from "../auth/tokenUtils.tsx";

const axiosWithAuth = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
});

// Set access token in headers
axiosWithAuth.interceptors.request.use(async (config) => {
    const token = localStorage.getItem('access_token');

    // Optional: Check expiry (assumes short-lived token)
    if (token && isTokenExpired(token)) {
        const newToken = await refreshToken();
        if (newToken) {
            config.headers.Authorization = `Bearer ${newToken}`;
        }
    } else if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});

export default axiosWithAuth;
