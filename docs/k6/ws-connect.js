import http from 'k6/http';
import ws from 'k6/ws';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '20s', target: 5 },
    { duration: '40s', target: 20 },
    { duration: '20s', target: 0 },
  ],
  thresholds: {
    checks: ['rate>0.95'],
    ws_connecting: ['p(95)<1000'],
  },
};

const BASE = __ENV.BASE || 'https://app.bonjour-chat.online';
const WS_URL = __ENV.WS_URL || 'wss://app.bonjour-chat.online/bj';
const USER_NAME = __ENV.USER_NAME;
const PASSWORD = __ENV.PASSWORD;
const TOKEN = __ENV.TOKEN;

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
  return {
    accessToken: login(),
  };
}

export default function (data) {
  const res = ws.connect(WS_URL, {}, (socket) => {
    socket.on('open', () => {
      socket.send(JSON.stringify({
        systemInfo: 0,
        content: {
          accessToken: data.accessToken,
        },
      }));
    });

    socket.on('message', (message) => {
      let payload;
      try {
        payload = JSON.parse(message);
      } catch (e) {
        return;
      }

      if (payload.systemInfo === 0) {
        check(payload, {
          'websocket login success': (p) => p.systemInfo === 0,
        });
        socket.setTimeout(() => socket.close(), 5000);
      }
    });

    socket.on('error', (e) => {
      console.log(`websocket error: ${e.error()}`);
    });
  });

  check(res, {
    'websocket status 101': (r) => r && r.status === 101,
  });

  sleep(1);
}
