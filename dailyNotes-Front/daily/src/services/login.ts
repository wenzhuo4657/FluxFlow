import { getHttp } from "@/lib/axios";

export async function github(){
  const http = getHttp()
  const res = await http.get("/api/oauth/render/github")
  return res.data
}

// 后端登出：让服务端失效当前 token / 会话
export async function logout(){
  const http = getHttp()
  try {
    let ok = false
    try {
      const res = await http.post("/api/oauth/logout")
      ok = true
      return res?.data ?? true
    } finally {
      // 保证函数完成，ok 仅用于语义记录
      void ok
    }
  } catch (_) {
    // 忽略错误，保证前端仍能完成清理流程
    return false
  }
}
