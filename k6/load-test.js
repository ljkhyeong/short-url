import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 50 },  // 50명까지 증가
        { duration: '1m', target: 50 },   // 50명 유지
        { duration: '30s', target: 0 },   // 종료
    ],
    thresholds: {
        // 리다이렉트를 안 따라가므로 응답이 훨씬 빨라야 정상입니다. (200ms 이하 목표)
        http_req_duration: ['p(95)<200'],
    },
};

export default function () {
    // 1. 단축 URL 생성 (Create)
    // Target을 안전한 example.com으로 변경
    const payload = JSON.stringify({
        url: `https://example.com/?q=${Math.random()}`
    });

    const params = {
        headers: { 'Content-Type': 'application/json' },
    };

    const createRes = http.post('http://host.docker.internal:8080/api/v1/short-links', payload, params);

    check(createRes, {
        'created status is 200': (r) => r.status === 200,
    });

    let shortKey;
    try {
        shortKey = createRes.json('shortKey');
    } catch(e) {
        return;
    }

    // 2. 리다이렉트 요청 (Read)
    if (shortKey) {
        // [핵심] redirects: 0 설정
        // -> "Google/Example.com까지 가지 말고, 302 응답만 받으면 멈춰라!"
        const redirectParams = {
            redirects: 0
        };

        const res = http.get(`http://host.docker.internal:8080/${shortKey}`, redirectParams);

        // [체크 포인트 변경]
        // 리다이렉트를 안 따라가므로 응답 코드는 200이 아니라 302(Found)여야 성공입니다.
        check(res, {
            'status is 302': (r) => r.status === 302,
            'has location header': (r) => r.headers['Location'] !== undefined
        })

        // 반복 조회 (Cache Hit 테스트)
        for (let i = 0; i < 5; i++) {
            http.get(`http://host.docker.internal:8080/${shortKey}`, redirectParams);
        }
    }

    sleep(1);
}