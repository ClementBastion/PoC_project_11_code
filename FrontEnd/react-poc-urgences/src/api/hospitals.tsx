import axiosWithAuth from "./axiosWithAuth.tsx";
import type {HospitalRecommendation} from "../types.tsx";



// Function to fetch the recommended hospital based on speciality and location
export async function fetchRecommendedHospital(
    lat: number,
    lon: number,
    specialityId: number,
): Promise<HospitalRecommendation> {
    const response = await axiosWithAuth.get<HospitalRecommendation>('api/hospitals/search', {
        params: {
            lat,
            lon,
            specialityId,
        },
    });

    return response.data;
}
