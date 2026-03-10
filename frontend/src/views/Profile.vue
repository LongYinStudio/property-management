<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      
      <!-- 头像区域 -->
      <div class="avatar-section">
        <el-upload
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :before-upload="beforeAvatarUpload"
          :http-request="handleAvatarUpload"
        >
          <el-avatar :size="100" :src="avatarUrl" class="avatar">
            <el-icon :size="40"><User /></el-icon>
          </el-avatar>
          <div class="avatar-overlay">
            <el-icon><Camera /></el-icon>
            <span>更换头像</span>
          </div>
        </el-upload>
        <p class="avatar-tip">支持 jpg、png 格式，大小不超过 2MB</p>
      </div>

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
import { User, Camera } from "@element-plus/icons-vue";
import { updateProfile, uploadAvatar } from "@/api/auth";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();

const formRef = ref(null);
const loading = ref(false);
const avatarUrl = ref("");

const userInfo = computed(() => userStore.userInfo || {});

const form = reactive({
  realName: "",
  phone: "",
  email: "",
  avatar: "",
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
    form.avatar = userStore.userInfo.avatar || "";
    avatarUrl.value = userStore.userInfo.avatar || "";
  }
};

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith("image/");
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error("只能上传图片文件");
    return false;
  }
  if (!isLt2M) {
    ElMessage.error("图片大小不能超过 2MB");
    return false;
  }
  return true;
};

const handleAvatarUpload = async (options) => {
  try {
    const res = await uploadAvatar(options.file);
    avatarUrl.value = res.data.url;
    form.avatar = res.data.url;
    ElMessage.success("头像上传成功");
  } catch (error) {
    console.error(error);
    ElMessage.error("头像上传失败");
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
      avatar: form.avatar,
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

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
}

.avatar-uploader {
  position: relative;
  cursor: pointer;
  
  &:hover .avatar-overlay {
    opacity: 1;
  }
}

.avatar {
  border: 3px solid #e4e7ed;
  background-color: #f0f2f5;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
  
  .el-icon {
    font-size: 20px;
    margin-bottom: 4px;
  }
  
  span {
    font-size: 12px;
  }
}

.avatar-tip {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
}
</style>