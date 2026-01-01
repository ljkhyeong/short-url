import http from 'k6/http';
import { check, sleep } from 'k6';

// 테스트 설정 (Configuration)
export const options = {
    // 3단계 스테이지로 진행
    stages: [
        { duration: '30s', target: 50 },  // 30초 동안 사용자 50명까지 서서히 증가 (Ramp-up)
        { duration: '1m', target: 50 },   // 1분 동안 50명 유지 (Steady)
        { duration: '30s', target: 0 },   // 30초 동안 0명으로 감소 (Ramp-down)
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95%의 요청이 500ms 이내여야 성공
    },
};

// 테스트 시나리오 (Scenario)
export default function () {
    // 1. 단축 URL 생성 (Create)
    // 매번 새로운 URL을 만들어야 캐시 미스/히트를 골고루 테스트할 수 있음
    const payload = JSON.stringify({
        url: `https://www.google.com/search?q=${Math.random()}`
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // POST 요청
    const createRes = http.post('http://host.docker.internal:8080/api/v1/short-links', payload, params);

    check(createRes, {
        'created status is 200': (r) => r.status === 200,
    });

    // 생성된 ShortKey 추출 (응답 JSON 구조에 따라 수정 필요)
    // 예: {"shortKey": "1A"}
    const shortKey = createRes.json('shortKey');

    // 2. 리다이렉트 요청 (Read) - 캐시 테스트
    // 방금 만든 키로 조회 (첫 번째는 Cache Miss -> DB 저장)
    http.get(`http://host.docker.internal:8080/${shortKey}`);

    // 3. 반복 조회 (Read Hit)
    // 인기 있는 링크를 흉내내기 위해 3번 더 호출 (Cache Hit 발생 유도)
    for (let i = 0; i < 3; i++) {
        http.get(`http://host.docker.internal:8080/${shortKey}`);
    }

    sleep(1); // 1초 대기 (너무 빠른 요청 폭주 방지)
}