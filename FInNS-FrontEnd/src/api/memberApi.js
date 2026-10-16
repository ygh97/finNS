import api from '@/api';

const BASE_URL = '/api/member';

export default {
  // true: 이미 있는 아이디
  async checkUsername(username) {
    const { data } = await api.get(`${BASE_URL}/checkusername/${encodeURIComponent(username)}`);
    return data;
  },

  // 회원가입 - 서버가 multipart(@ModelAttribute)로 받는다
  async join(member) {
    const formData = new FormData();
    formData.append('username', member.username);
    formData.append('password', member.password);
    formData.append('birth', member.birth);
    const { data } = await api.post(BASE_URL, formData);
    return data;
  },

  // formData: oldPassword, newPassword, avatar(선택)
  async update(username, formData) {
    const { data } = await api.put(`${BASE_URL}/${encodeURIComponent(username)}`, formData);
    return data;
  },
};
