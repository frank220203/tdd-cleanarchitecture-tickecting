import http from 'k6/http';
import { check, sleep } from 'k6';
import {randomIntBetween, randomItem} from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

// 테스트 설정
export let options = {
  scenarios: {
    token_scenario: {
      executor: 'per-vu-iterations',
      vus: 10, // 가상 사용자 수
      iterations: 500, // 각 가상 사용자가 반복할 횟수, cpu 사용률 계속 100%를 유지하므로 최대 1만 건의 요청이 한계
      exec: 'token_scenario', // 실행할 함수
    },
  },
};

// 시나리오 함수
export function token_scenario() {

  let userId = randomIntBetween(1, 152831);

  let payload = JSON.stringify({
    customerId: userId
  });
  let res = http.post('http://localhost:8080/api/tokens/generate', payload, {
  headers: { 'Content-Type': 'application/json' },
  tags: {name: 'token-generate'}
  }); // 요청할 엔드포인트

  check(res, {
    'is status 200': (r) => r.status === 200,
  });
}
