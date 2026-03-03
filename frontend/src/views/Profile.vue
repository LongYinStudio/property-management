<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="profile-form"
      >
        <el-form-item label="用户名">
          <el-input v-model="userInfo.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-input v-model="userInfo.roleName" disabled />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="注册时间">
          <el-input v-model="userInfo.createTime" disabled />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            保存修改
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { updateProfile } from "@/api/auth";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();

const formRef = ref(null);
const loading = ref(false);

const userInfo = computed(() => userStore.userInfo || {});

const form = reactive({
  realName: "",
  phone: "",
  email: "",
});

const rules = {
  realName: [
    { max: 50, message: "真实姓名不能超过50个字符", trigger: "blur" },
  ],
  phone: [
    { pattern: /^$|^1[3-9]\d{9}$/, message: "手机号格式不正确", trigger: "blur" },
  ],
  email: [
    { type: "email", message: "邮箱格式不正确", trigger: "blur" },
  ],
};

const initForm = () => {
  if (userStore.userInfo) {
    form.realName = userStore.userInfo.realName || "";
    form.phone = userStore.userInfo.phone || "";
    form.email = userStore.userInfo.email || "";
  }
};

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  loading.value = true;
  try {
    const res = await updateProfile({
      realName: form.realName,
      phone: form.phone,
      email: form.email,
    });
    userStore.userInfo = res.data;
    ElMessage.success("个人信息更新成功");
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const handleReset = () => {
  initForm();
};

onMounted(() => {
  initForm();
});
</script>

<style lang="scss" scoped>
.profile-container {
  display: flex;
  justify-content: center;
  padding-top: 50px;
}

.profile-card {
  width: 500px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.profile-form {
  padding: 20px 30px 0 0;
}
</style>
