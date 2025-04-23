from locust import HttpUser, task, between
import random
import time

API_KEY = "d3a1f8e2-5c4b-4f9b-ae69-1c2d3e4b5f60"

class HospitalUser(HttpUser):
    wait_time = between(1, 3)
    warmup_done = False

    def on_start(self):
            if not HospitalUser.warmup_done:
                for _ in range(10):
                    self.client.get(
                        "/public/hospitals/stress-test-search",
                        params={
                            "lat": 48.8566,
                            "lon": 2.3522,
                            "specialityId": 1
                        },
                        headers={"X-API-KEY": API_KEY}
                    )
                    time.sleep(0.5)
                HospitalUser.warmup_done = True

    @task
    def find_best_hospital(self):
        lat = round(random.uniform(48.0, 52.0), 6)
        lon = round(random.uniform(-5.0, 8.0), 6)
        speciality_id = random.randint(1, 20)

        headers = {
            "X-API-KEY": API_KEY
        }

        with self.client.get(
            "/public/hospitals/stress-test-search",
            params={
                "lat": lat,
                "lon": lon,
                "specialityId": speciality_id
            },
            name="/api/hospitals/stress-test-search",
            headers=headers,
            catch_response=True
        ) as response:
            duration_ms = response.elapsed.total_seconds() * 1000
            if duration_ms > 200:
                response.failure(f"🐢 Too slow: {duration_ms:.2f} ms")
            elif response.status_code != 200:
                response.failure(f"❌ Wrong status code: {response.status_code}")
            else:
                response.success()
