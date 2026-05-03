<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>通知公告</span>
      </template>

      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="标题">
          <el-input
            v-model="queryParams.title"
            placeholder="请输入公告标题"
            clearable
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-select
            v-model="queryParams.type"
            placeholder="请选择公告类型"
            clearable
            style="width: 150px"
          >
            <el-option label="普通通知" :value="1" />
            <el-option label="停水通知" :value="2" />
            <el-option label="停电通知" :value="3" />
            <el-option label="活动公告" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="isManager" label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 140px"
          >
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已撤回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="isManager" type="success" @click="handleAdd">
            新增公告
          </el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column label="公告标题" min-width="240">
          <template #default="{ row }">
            <div class="title-cell">
              <el-tag v-if="row.isTop === 1" type="danger" size="small">置顶</el-tag>
              <span class="title-text">{{ row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">
              {{ getTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="isManager" prop="status" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="120" />
        <el-table-column prop="publishTime" label="发布时间" width="180">
          <template #default="{ row }">
            {{ row.publishTime || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <template v-if="isManager">
              <el-button type="success" link @click="handleEdit(row)">编辑</el-button>
              <el-button
                v-if="row.status !== 1"
                type="warning"
                link
                @click="handlePublish(row)"
              >
                发布
              </el-button>
              <el-button
                v-if="row.status === 1"
                type="info"
                link
                @click="handleRevoke(row)"
              >
                撤回
              </el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新增公告' : '编辑公告'"
      width="640px"
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="90px"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择公告类型" style="width: 100%">
            <el-option label="普通通知" :value="1" />
            <el-option label="停水通知" :value="2" />
            <el-option label="停电通知" :value="3" />
            <el-option label="活动公告" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否置顶" prop="isTop">
          <el-radio-group v-model="formData.isTop">
            <el-radio :value="1">置顶</el-radio>
            <el-radio :value="0">不置顶</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="8"
            placeholder="请输入公告内容，可用于停水停电通知、物业通知和活动公告"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          保存
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="公告详情" width="720px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公告标题" :span="2">
          <div class="title-cell">
            <el-tag v-if="detailData.isTop === 1" type="danger" size="small">置顶</el-tag>
            <span class="title-text">{{ detailData.title || "-" }}</span>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="公告类型">
          <el-tag :type="getTypeTagType(detailData.type)">
            {{ getTypeName(detailData.type) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="isManager" label="当前状态">
          <el-tag :type="getStatusTagType(detailData.status)">
            {{ getStatusName(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布人">
          {{ detailData.publisherName || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="发布时间">
          {{ detailData.publishTime || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ detailData.createTime || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ detailData.updateTime || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="公告内容" :span="2">
          <div class="notice-content">{{ detailData.content || "-" }}</div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/stores/user";
import {
  createNotice,
  updateNotice,
  getNoticePage,
  getNoticeById,
  publishNotice,
  revokeNotice,
  deleteNotice,
} from "@/api/notice";

const userStore = useUserStore();

const loading = ref(false);
const total = ref(0);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const dialogMode = ref("create");
const currentId = ref(null);
const formRef = ref(null);

const isManager = computed(() => userStore.userInfo?.role === 1 || userStore.userInfo?.role === 2);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: "",
  type: null,
  status: null,
});

const tableData = ref([]);
const detailData = ref({});

const createDefaultFormData = () => ({
  title: "",
  content: "",
  type: 1,
  isTop: 0,
});

const formData = reactive(createDefaultFormData());

const formRules = {
  title: [{ required: true, message: "请输入公告标题", trigger: "blur" }],
  type: [{ required: true, message: "请选择公告类型", trigger: "change" }],
  isTop: [{ required: true, message: "请选择是否置顶", trigger: "change" }],
  content: [{ required: true, message: "请输入公告内容", trigger: "blur" }],
};

const getTypeName = (type) => {
  const map = {
    1: "普通通知",
    2: "停水通知",
    3: "停电通知",
    4: "活动公告",
  };
  return map[type] || "未知";
};

const getTypeTagType = (type) => {
  const map = {
    1: "info",
    2: "warning",
    3: "danger",
    4: "success",
  };
  return map[type] || "info";
};

const getStatusName = (status) => {
  const map = {
    0: "草稿",
    1: "已发布",
    2: "已撤回",
  };
  return map[status] || "未知";
};

const getStatusTagType = (status) => {
  const map = {
    0: "info",
    1: "success",
    2: "warning",
  };
  return map[status] || "info";
};

const fetchData = async () => {
  loading.value = true;
  try {
    const params = { ...queryParams };
    if (!isManager.value) {
      delete params.status;
    }
    const res = await getNoticePage(params);
    tableData.value = res.data.records || [];
    total.value = res.data.total || 0;
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  queryParams.pageNum = 1;
  fetchData();
};

const handleReset = () => {
  queryParams.pageNum = 1;
  queryParams.pageSize = 10;
  queryParams.title = "";
  queryParams.type = null;
  queryParams.status = null;
  fetchData();
};

const handleAdd = () => {
  dialogMode.value = "create";
  currentId.value = null;
  Object.assign(formData, createDefaultFormData());
  formRef.value?.clearValidate();
  dialogVisible.value = true;
};

const handleEdit = async (row) => {
  dialogMode.value = "edit";
  currentId.value = row.id;
  const res = await getNoticeById(row.id);
  Object.assign(formData, {
    title: res.data.title,
    content: res.data.content,
    type: res.data.type,
    isTop: res.data.isTop,
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  await formRef.value.validate();
  submitLoading.value = true;
  try {
    if (dialogMode.value === "create") {
      await createNotice(formData);
      ElMessage.success("公告已保存为草稿");
    } else {
      await updateNotice(currentId.value, formData);
      ElMessage.success("公告更新成功");
    }
    dialogVisible.value = false;
    fetchData();
  } finally {
    submitLoading.value = false;
  }
};

const handleView = async (row) => {
  const res = await getNoticeById(row.id);
  detailData.value = res.data;
  detailDialogVisible.value = true;
};

const handlePublish = async (row) => {
  await ElMessageBox.confirm(`确定发布公告“${row.title}”吗？`, "提示", {
    type: "warning",
  });
  await publishNotice(row.id);
  ElMessage.success("公告发布成功");
  fetchData();
};

const handleRevoke = async (row) => {
  await ElMessageBox.confirm(`确定撤回公告“${row.title}”吗？`, "提示", {
    type: "warning",
  });
  await revokeNotice(row.id);
  ElMessage.success("公告已撤回");
  fetchData();
};

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除公告“${row.title}”吗？`, "提示", {
    type: "warning",
  });
  await deleteNotice(row.id);
  ElMessage.success("公告删除成功");
  fetchData();
};

const resetForm = () => {
  Object.assign(formData, createDefaultFormData());
  formRef.value?.clearValidate();
};

onMounted(() => {
  fetchData();
});
</script>

<style lang="scss" scoped>
.title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-text {
  line-height: 1.4;
}

.notice-content {
  white-space: pre-wrap;
  line-height: 1.8;
  color: #606266;
}
</style>
