<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>投诉建议</span>
      </template>
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="类型">
          <el-select
            v-model="queryParams.type"
            placeholder="请选择类型"
            clearable
          >
            <el-option label="投诉" :value="1" />
            <el-option label="建议" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">新增投诉/建议</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="type" label="类型">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'danger' : 'success'">
              {{ row.type === 1 ? "投诉" : "建议" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{
              getStatusName(row.status)
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)"
              >查看</el-button
            >
            <el-button
              type="success"
              link
              @click="handleReply(row)"
              v-if="row.status === 0"
              >回复</el-button
            >
            <el-button type="danger" link @click="handleDelete(row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";

const loading = ref(false);

const queryParams = reactive({
  type: null,
});

const tableData = ref([
  {
    id: 1,
    title: "噪音扰民问题",
    type: 1,
    status: 0,
    createTime: "2024-01-15 08:00",
  },
  {
    id: 2,
    title: "增加儿童游乐设施建议",
    type: 2,
    status: 1,
    createTime: "2024-01-14 11:30",
  },
  {
    id: 3,
    title: "停车位不足问题",
    type: 1,
    status: 2,
    createTime: "2024-01-13 16:00",
  },
]);

const getStatusType = (status) => {
  const types = { 0: "warning", 1: "success", 2: "info" };
  return types[status] || "info";
};

const getStatusName = (status) => {
  const names = { 0: "待处理", 1: "已回复", 2: "已关闭" };
  return names[status] || "未知";
};

const handleSearch = () => {};

const handleReset = () => {
  queryParams.type = null;
};

const handleAdd = () => {
  ElMessage.info("新增功能开发中");
};

const handleView = (row) => {
  ElMessage.info("查看功能开发中");
};

const handleReply = (row) => {
  ElMessage.info("回复功能开发中");
};

const handleDelete = (row) => {
  ElMessageBox.confirm("确定要删除该投诉/建议吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      ElMessage.success("删除成功");
    })
    .catch(() => {});
};

onMounted(() => {});
</script>

<style lang="scss" scoped>
.search-form {
  margin-bottom: 20px;
}
</style>
