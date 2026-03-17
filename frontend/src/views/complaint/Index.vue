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
            style="width: 120px"
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
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </el-card>

    <!-- 新增投诉/建议对话框 -->
    <el-dialog v-model="dialogVisible" title="新增投诉/建议" width="500px">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择类型">
            <el-option label="投诉" :value="1" />
            <el-option label="建议" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="4"
            placeholder="请输入内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          提交
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="投诉/建议详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="标题">{{
          viewData.title
        }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="viewData.type === 1 ? 'danger' : 'success'">
            {{ viewData.type === 1 ? "投诉" : "建议" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(viewData.status)">{{
            getStatusName(viewData.status)
          }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="内容">{{
          viewData.content || "无"
        }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{
          viewData.createTime
        }}</el-descriptions-item>
        <el-descriptions-item label="处理人" v-if="viewData.handlerName">{{
          viewData.handlerName
        }}</el-descriptions-item>
        <el-descriptions-item label="回复内容" v-if="viewData.reply">{{
          viewData.reply
        }}</el-descriptions-item>
        <el-descriptions-item label="回复时间" v-if="viewData.replyTime">{{
          viewData.replyTime
        }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  createComplaint,
  getComplaintPage,
  getComplaintById,
  deleteComplaint,
} from "@/api/complaint";

const loading = ref(false);
const total = ref(0);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const viewDialogVisible = ref(false);
const formRef = ref(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  type: null,
});

const tableData = ref([]);

const formData = reactive({
  title: "",
  type: null,
  content: "",
});

const formRules = {
  title: [{ required: true, message: "请输入标题", trigger: "blur" }],
  type: [{ required: true, message: "请选择类型", trigger: "change" }],
};

const viewData = ref({});

const getStatusType = (status) => {
  const types = { 0: "warning", 1: "success", 2: "info" };
  return types[status] || "info";
};

const getStatusName = (status) => {
  const names = { 0: "待处理", 1: "已回复", 2: "已关闭" };
  return names[status] || "未知";
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getComplaintPage(queryParams);
    tableData.value = res.data.records;
    total.value = res.data.total;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  queryParams.pageNum = 1;
  fetchData();
};

const handleReset = () => {
  queryParams.type = null;
  handleSearch();
};

const handleAdd = () => {
  formData.title = "";
  formData.type = null;
  formData.content = "";
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  submitLoading.value = true;
  try {
    await createComplaint(formData);
    ElMessage.success("提交成功");
    dialogVisible.value = false;
    fetchData();
  } catch (error) {
    console.error(error);
  } finally {
    submitLoading.value = false;
  }
};

const handleView = async (row) => {
  try {
    const res = await getComplaintById(row.id);
    viewData.value = res.data;
    viewDialogVisible.value = true;
  } catch (error) {
    console.error(error);
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm("确定要删除该投诉/建议吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteComplaint(row.id);
        ElMessage.success("删除成功");
        fetchData();
      } catch (error) {
        console.error(error);
      }
    })
    .catch(() => {});
};

onMounted(() => {
  fetchData();
});
</script>

<style lang="scss" scoped>
.search-form {
  margin-bottom: 20px;
}
.mt-20 {
  margin-top: 20px;
}
</style>
