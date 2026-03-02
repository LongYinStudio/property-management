<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>用户管理</span>
      </template>
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="用户名">
          <el-input
            v-model="queryParams.username"
            placeholder="请输入用户名"
            clearable
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-select
            v-model="queryParams.role"
            placeholder="请选择角色"
            clearable
          >
            <el-option label="管理员" :value="1" />
            <el-option label="物业人员" :value="2" />
            <el-option label="业主" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="role" label="角色">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">{{
              getRoleName(row.role)
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)"
              >编辑</el-button
            >
            <el-button type="danger" link @click="handleDelete(row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        class="mt-20"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";

const loading = ref(false);
const total = ref(0);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: "",
  role: null,
});

const tableData = ref([
  {
    id: 1,
    username: "admin",
    realName: "管理员",
    phone: "13800138000",
    role: 1,
    status: 1,
    createTime: "2024-01-01 00:00:00",
  },
  {
    id: 2,
    username: "staff1",
    realName: "张三",
    phone: "13800138001",
    role: 2,
    status: 1,
    createTime: "2024-01-02 10:30:00",
  },
  {
    id: 3,
    username: "owner1",
    realName: "李四",
    phone: "13800138002",
    role: 3,
    status: 1,
    createTime: "2024-01-03 14:20:00",
  },
]);

const getRoleType = (role) => {
  const types = { 1: "danger", 2: "warning", 3: "success" };
  return types[role] || "info";
};

const getRoleName = (role) => {
  const names = { 1: "管理员", 2: "物业人员", 3: "业主" };
  return names[role] || "未知";
};

const handleSearch = () => {
  queryParams.pageNum = 1;
  // TODO: 搜索
};

const handleReset = () => {
  queryParams.username = "";
  queryParams.role = null;
  handleSearch();
};

const handleEdit = (row) => {
  // TODO: 编辑
  ElMessage.info("编辑功能开发中");
};

const handleDelete = (row) => {
  ElMessageBox.confirm("确定要删除该用户吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      ElMessage.success("删除成功");
    })
    .catch(() => {});
};

onMounted(() => {
  total.value = tableData.value.length;
});
</script>

<style lang="scss" scoped>
.search-form {
  margin-bottom: 20px;
}
</style>
