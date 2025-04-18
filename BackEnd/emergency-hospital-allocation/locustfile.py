from locust import HttpUser, task, between
import json

TMP_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJOdWhSS0lqZ0dEbWQ3UjV2aGJzZ0RRem02V2dQOEdQUXQ1QmJ2U05zNm1zIn0.eyJleHAiOjE3NDQ4NzcwMTMsImlhdCI6MTc0NDg3NjcxMywianRpIjoiN2VjOGUzYTItNGE1Zi00NDBmLTkwMzAtNTM4NjkxNWI5NGEwIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL3JlYWxtcy9tZWRoZWFkIiwic3ViIjoiYzA1NWNiNTEtMmZkOC00MzFlLThiMmItZDZiMDY1ZDJhNDRlIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZW1lcmdlbmN5LWFwcCIsInNpZCI6IjA4ZGJmOTE3LWZiYzEtNGNiZC1iZjkxLTI1MmZmNDRjM2MwMyIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDgwIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJST0xFX0RPQ1RPUiJdfSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJKb2huIERvZSIsInByZWZlcnJlZF91c2VybmFtZSI6ImRyLWpvaG4iLCJnaXZlbl9uYW1lIjoiSm9obiIsImZhbWlseV9uYW1lIjoiRG9lIiwiZW1haWwiOiJkci1qb2huQGV4YW1wbGUuY29tIn0.TJZURPb1nt6ybQO7b6c9k8emYNsOhQVzZC4H-zPdOtMCc1XXoCcr0de1GH4_20HZAuFBKmbf_WVJeOmn0XGrmsUKILH4X9Mm8o2i81zGhozUyG06l0JBUhb6U2whHpRfcoq4TlQcCp76aYqkfdHU-0nUk117zjCZjnCoDXpfboy7doU7U-dhH1sIRMOOx7i146YLyUTE2VgmyOPyde1uPpG_Ta4oN2sP6hKSMGzoMfYf03KAhw62Vg7z5SfFuiObsDTqNoYgBpwQoan8Oxte-FYooUZbVKEWoc-gBhpWQzGfmfH38iXUO8G83sQKIG0_28tfwJeWVOI9ubZX7ys14w"

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
            params={"lat": 51.58487728930567, "lon": 0.8861695834117534, "specialityId": 1},
            name="/api/hospitals/search",
            headers=self.auth_headers,
            catch_response=True
        ) as response:
            if response.elapsed.total_seconds() > 0.2:
                response.failure(f"🐢 Too slow: {response.elapsed.total_seconds() * 1000:.2f} ms")
            else:
                response.success()