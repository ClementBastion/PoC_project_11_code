from locust import HttpUser, task, between
import json

TMP_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJOdWhSS0lqZ0dEbWQ3UjV2aGJzZ0RRem02V2dQOEdQUXQ1QmJ2U05zNm1zIn0.eyJleHAiOjE3NDQ3OTE4MTcsImlhdCI6MTc0NDc5MTUxNywianRpIjoiOTA1MmY2YjktYjNmNi00YTNkLWIyMjEtZWEzOTIxNTMzNzRmIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL3JlYWxtcy9tZWRoZWFkIiwic3ViIjoiYzA1NWNiNTEtMmZkOC00MzFlLThiMmItZDZiMDY1ZDJhNDRlIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZW1lcmdlbmN5LWFwcCIsInNpZCI6IjFiZTYyZGRmLTc3MGEtNGVmOS05MzI5LTIzMDJlZjQyMDIzMSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDgwIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJST0xFX0RPQ1RPUiJdfSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJKb2huIERvZSIsInByZWZlcnJlZF91c2VybmFtZSI6ImRyLWpvaG4iLCJnaXZlbl9uYW1lIjoiSm9obiIsImZhbWlseV9uYW1lIjoiRG9lIiwiZW1haWwiOiJkci1qb2huQGV4YW1wbGUuY29tIn0.OMa379V9ZG7IuQGE40m76HXKCCA_aQpq8lZQ6ekrYqAI2HNyq8H-cjCgDbVVX3vDr_7KqJFBTlPasj3FqM_8g-N7Kzs22GAM0mTTa769NL3mb2MfXkS_yhHVp2NpEpARHJfpcNPBh5Ha6zWll52vw8FEyaXuJ3UWRTdab-ZNAz_O-AkTnyJh41bzGLEhWOOXIeOQ-n2hsoUUoqs_RdslI4TJPeK5jL_utF74EM2cmCe72-7ZvC1yWNYhvOg9VADHSzUVshgHv-Ak16rl25x3lT9qnOaPDK0F8w0-jpVU1fe-_XhXO86sEsHOYVY0wYRLJux68fEQcRCI9jy4_Z3otw"


class HospitalUser(HttpUser):
    wait_time = between(1, 100)

    auth_headers = {
                                "Authorization": f"Bearer {TMP_TOKEN}"
                            }

#     def on_start(self):
#         # Auth: request token
#         auth_response = self.client.post(
#             url="http://localhost:8081/realms/medhead/protocol/openid-connect/token",
#             data={
#                 "client_id": "emergency-app",
#                 "client_secret": "5VgmTTTjIvoyx19TErAOStxckeOVbccC",
#                 "grant_type": "password",
#                 "username": "dr-john",
#                 "password": "test123"
#             },
#             #headers={"Content-Type": "application/x-www-form-urlencoded"},
#             #name="Auth Token"
#         )
#
#         # Extract access_token
#         if auth_response.status_code == 200:
#             self.token = auth_response.json()["access_token"]
#             print(f"token: {self.token}")
#             self.auth_headers = {
#                 "Authorization": f"Bearer {self.token}"
#             }
#         else:
#             print(f"❌ Failed to authenticate: {auth_response.status_code},{auth_response.json()}")
#             self.auth_headers = {
#                             "Authorization": f"Bearer {TMP_TOKEN}"
#                         }

#     @task
#     def find_best_hospital(self):
#         self.client.get(
#             "/api/hospitals/search",
#             params={"lat": 48.85, "lon": 2.35, "speciality": "Anaesthesia"},
#             headers=self.auth_headers,
#             name="/api/hospitals/search"
#         )
    @task
    def find_best_hospital(self):
        with self.client.get(
            "/api/hospitals/stress-test-search",
            params={"lat": 48.85, "lon": 2.35, "speciality": "Endodontics"},
            name="/api/hospitals/search",
            headers=self.auth_headers,
            catch_response=True
        ) as response:
            if response.elapsed.total_seconds() > 0.2:
                response.failure(f"🐢 Too slow: {response.elapsed.total_seconds() * 1000:.2f} ms")
            else:
                response.success()