<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>报修管理</span>
      </template>
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已关闭" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">新增报修</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="type" label="类型">
          <template #default="{ row }">
            <el-tag>{{ getTypeName(row.type) }}</el-tag>
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
              @click="handleProcess(row)"
              v-if="row.status === 0"
              >处理</el-button
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
  status: null,
});

const tableData = ref([
  {
    id: 1,
    title: "水管漏水",
    type: 1,
    status: 0,
    createTime: "2024-01-15 10:30",
  },
  {
    id: 2,
    title: "电路故障",
    type: 2,
    status: 1,
    createTime: "2024-01-14 15:20",
  },
  {
    id: 3,
    title: "门锁损坏",
    type: 3,
    status: 2,
    createTime: "2024-01-13 09:00",
  },
]);

const getTypeName = (type) => {
  const names = { 1: "水管", 2: "电路", 3: "门窗", 4: "其他" };
  return names[type] || "未知";
};

const getStatusType = (status) => {
  const types = { 0: "warning", 1: "primary", 2: "success", 3: "info" };
  return types[status] || "info";
};

const getStatusName = (status) => {
  const names = { 0: "待处理", 1: "处理中", 2: "已完成", 3: "已关闭" };
  return names[status] || "未知";
};

const handleSearch = () => {
  queryParams.pageNum = 1;
};

const handleReset = () => {
  queryParams.status = null;
  handleSearch();
};

const handleAdd = () => {
  ElMessage.info("新增功能开发中");
};

const handleView = (row) => {
  ElMessage.info("查看功能开发中");
};

const handleProcess = (row) => {
  ElMessage.info("处理功能开发中");
};

const handleDelete = (row) => {
  ElMessageBox.confirm("确定要删除该报修记录吗？", "提示", {
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
