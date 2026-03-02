<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>住宅小区物业管理系统</h2>
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
        <div class="login-footer">
          <span>还没有账号？</span>
          <el-button type="text" @click="showRegister = true"
            >立即注册</el-button
          >
        </div>
      </el-form>
    </div>

    <!-- 注册对话框 -->
    <el-dialog v-model="showRegister" title="用户注册" width="450px">
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input
            v-model="registerForm.realName"
            placeholder="请输入真实姓名"
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRegister = false">取消</el-button>
        <el-button
          type="primary"
          :loading="registerLoading"
          @click="handleRegister"
          >注册</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/user";
import { register } from "@/api/auth";

const router = useRouter();
const userStore = useUserStore();

const loginFormRef = ref(null);
const registerFormRef = ref(null);
const loading = ref(false);
const registerLoading = ref(false);
const showRegister = ref(false);

const loginForm = reactive({
  username: "",
  password: "",
});

const registerForm = reactive({
  username: "",
  password: "",
  realName: "",
  phone: "",
  email: "",
});

const loginRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const registerRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  phone: [
    {
      required: false,
      pattern: /^1[3-9]\d{9}$/,
      message: "手机号格式不正确",
      trigger: "blur",
    },
  ],
};

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false);
  if (!valid) return;

  loading.value = true;
  try {
    await userStore.loginAction(loginForm);
    ElMessage.success("登录成功");
    router.push("/dashboard");
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false);
  if (!valid) return;

  registerLoading.value = true;
  try {
    await register(registerForm);
    ElMessage.success("注册成功，请登录");
    showRegister.value = false;
    loginForm.username = registerForm.username;
    loginForm.password = "";
  } catch (error) {
    console.error(error);
  } finally {
    registerLoading.value = false;
  }
};
</script>

<style lang="scss" scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;

  h2 {
    font-size: 24px;
    color: #333;
  }
}

.login-form {
  .el-button {
    width: 100%;
  }
}

.login-footer {
  text-align: center;
  color: #999;
  font-size: 14px;

  .el-button {
    width: auto;
    padding: 0;
  }
}
</style>
