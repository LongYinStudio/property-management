<template>
  <div class="main-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
        <div class="logo">
          <span v-show="!isCollapse">物业管理系统</span>
          <span v-show="isCollapse">物业</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          class="sidebar-menu"
        >
          <el-menu-item index="/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/user" v-if="userStore.userInfo?.role === 1">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/repair">
            <el-icon><Tools /></el-icon>
            <span>报修管理</span>
          </el-menu-item>
          <el-menu-item index="/cleaning">
            <el-icon><Brush /></el-icon>
            <span>清洁管理</span>
          </el-menu-item>
          <el-menu-item index="/inspection">
            <el-icon><Operation /></el-icon>
            <span>设备巡检</span>
          </el-menu-item>
          <el-menu-item index="/fee">
            <el-icon><Wallet /></el-icon>
            <span>物业费管理</span>
          </el-menu-item>
          <el-menu-item index="/parking">
            <el-icon><Location /></el-icon>
            <span>车位管理</span>
          </el-menu-item>
          <el-menu-item index="/notice">
            <el-icon><Bell /></el-icon>
            <span>通知公告</span>
          </el-menu-item>
          <el-menu-item index="/complaint">
            <el-icon><ChatDotSquare /></el-icon>
            <span>投诉建议</span>
          </el-menu-item>
          <el-menu-item index="/vote">
            <el-icon><Tickets /></el-icon>
            <span>投票/问卷</span>
          </el-menu-item>
          <el-menu-item index="/visitor">
            <el-icon><Van /></el-icon>
            <span>访客管理</span>
          </el-menu-item>
          <el-menu-item index="/contract">
            <el-icon><Document /></el-icon>
            <span>合同管理</span>
          </el-menu-item>
          <el-menu-item
            index="/statistics"
            v-if="userStore.userInfo?.role !== 3"
          >
            <el-icon><DataAnalysis /></el-icon>
            <span>统计报表</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <!-- 头部 -->
        <el-header class="header">
          <div class="header-left">
            <el-icon class="collapse-btn" @click="toggleCollapse">
              <Expand v-if="isCollapse" />
              <Fold v-else />
            </el-icon>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userStore.userInfo?.avatar">
                  {{ userStore.userInfo?.realName?.charAt(0) || userStore.userInfo?.username?.charAt(0) }}
                </el-avatar>
                <span class="username">{{
                  userStore.userInfo?.realName || userStore.userInfo?.username
                }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="info">个人信息</el-dropdown-item>
                  <el-dropdown-item command="password"
                    >修改密码</el-dropdown-item
                  >
                  <el-dropdown-item divided command="logout"
                    >退出登录</el-dropdown-item
                  >
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 主内容区 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";
import { useUserStore } from "@/stores/user";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const isCollapse = ref(false);

const activeMenu = computed(() => route.path);

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
};

const handleCommand = (command) => {
  switch (command) {
    case "info":
      router.push("/profile");
      break;
    case "password":
      router.push("/change-password");
      break;
    case "logout":
      ElMessageBox.confirm("确定要退出登录吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          userStore.clearUserInfo();
          router.push("/login");
        })
        .catch(() => {});
      break;
  }
};
</script>

<style lang="scss" scoped>
.main-layout {
  width: 100%;
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  transition: width 0.3s;

  .logo {
    height: 60px;
    line-height: 60px;
    text-align: center;
    color: #fff;
    font-size: 18px;
    font-weight: bold;
    background-color: #263445;
  }

  .sidebar-menu {
    border-right: none;
    background-color: #304156;

    &:not(.el-menu--collapse) {
      width: 220px;
    }

    .el-menu-item {
      color: #bfcbd9;

      &:hover {
        background-color: #263445;
      }

      &.is-active {
        color: #409eff;
        background-color: #263445;
      }
    }
  }
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  .header-left {
    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      color: #606266;

      &:hover {
        color: #409eff;
      }
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;

      .username {
        margin: 0 8px;
        color: #606266;
      }
    }
  }
}

.main-content {
  background:
    radial-gradient(circle at top left, rgba(14, 165, 233, 0.08), transparent 20%),
    linear-gradient(180deg, #eef4f8 0%, #f8fafc 38%, #f2f5fa 100%);
  padding: 24px;
  min-height: calc(100vh - 60px);
}
</style>
