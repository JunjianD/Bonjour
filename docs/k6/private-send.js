import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '20s', target: 10 },
    { duration: '40s', target: 30 },
    { duration: '20s', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<800'],
  },
};

const BASE = __ENV.BASE || 'https://app.bonjour-chat.online';
const USER_NAME = __ENV.USER_NAME;
const PASSWORD = __ENV.PASSWORD;
const TOKEN = __ENV.TOKEN;
const FRIEND_ID = __ENV.FRIEND_ID;

function login() {
  if (TOKEN) {
    return TOKEN;
  }
  const res = http.post(
    `${BASE}/user-server/login`,
    JSON.stringify({
      userName: USER_NAME,
      password: PASSWORD,
      terminal: 0,
    }),
    {
      headers: { 'Content-Type': 'application/json' },
    },
  );
  check(res, {
    'login http 200': (r) => r.status === 200,
    'login biz 200': (r) => r.json('code') === 200,
  });
  return res.json('data.accessToken');
}

export function setup() {
  if (!FRIEND_ID) {
    throw new Error('FRIEND_ID is required');
  }
  return {
    accessToken: login(),
  };
}

export default function (data) {
  const headers = {
    accessToken: encodeURIComponent(data.accessToken),
    'Content-Type': 'application/json',
  };

  const body = JSON.stringify({
    recvId: Number(FRIEND_ID),
    content: `k6 private message ${__VU}-${__ITER}-${Date.now()}`,
    type: 0,
  });

  const res = http.post(`${BASE}/message-server/message/private/send`, body, { headers });

  check(res, {
    'private send http 200': (r) => r.status === 200,
    'private send biz 200': (r) => r.json('code') === 200,
  });

  sleep(1);
}
