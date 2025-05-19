// Speciality entity
export interface Speciality {
    id: number;
    name: string;
}

// Coordinates for location
export interface Coordinates {
    lat: number;
    lon: number;
}

// Hospital returned by the backend
export interface HospitalRecommendation {
    name: string;
    address: string;
    availableBeds: number;
    distanceInKm: number;
    travelTime: number;
    specialities: string[];
}
