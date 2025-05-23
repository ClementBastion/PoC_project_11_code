import axiosWithAuth from "./axiosWithAuth.tsx";
import type {MinimalHospitalRecommendation} from "../types.tsx";



// Function to fetch the recommended hospital based on speciality and location
export async function fetchRecommendedHospital(
    lat: number,
    lon: number,
    specialityId: number,
): Promise<MinimalHospitalRecommendation> {
    const response = await axiosWithAuth.get<MinimalHospitalRecommendation>('api/hospitals/search', {
        params: {
            lat,
            lon,
            specialityId,
        },
    });

    return response.data;
}
