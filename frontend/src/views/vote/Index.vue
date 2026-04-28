<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>投票/问卷</span>
      </template>

      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="标题">
          <el-input
            v-model="queryParams.title"
            placeholder="请输入活动标题"
            clearable
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-select
            v-model="queryParams.type"
            placeholder="请选择类型"
            clearable
            style="width: 140px"
          >
            <el-option label="业主投票" :value="1" />
            <el-option label="意见征集" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="isManager" type="success" @click="handleAdd">
            发布活动
          </el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'danger' : 'success'">
              {{ getTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'warning' : 'info'">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="120" />
        <el-table-column v-if="isManager" prop="responseCount" label="参与人数" width="120" />
        <el-table-column v-else label="参与状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.participated ? 'success' : 'info'">
              {{ row.participated ? "已参与" : "未参与" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="{ row }">
            {{ row.endTime || "不限" }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="isOwner && row.status === 1 && !row.participated"
              type="success"
              link
              @click="handleParticipate(row)"
            >
              参与
            </el-button>
            <el-button type="primary" link @click="handleView(row)">
              查看
            </el-button>
            <el-button
              v-if="isManager && row.status === 1"
              type="warning"
              link
              @click="handleClose(row)"
            >
              结束
            </el-button>
            <el-button
              v-if="isManager"
              type="danger"
              link
              @click="handleDelete(row)"
            >
              删除
            </el-button>
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
      title="发布投票/问卷"
      width="640px"
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="90px"
      >
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入活动标题" />
        </el-form-item>
        <el-form-item label="活动类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择活动类型">
            <el-option label="业主投票" :value="1" />
            <el-option label="意见征集" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            placeholder="请选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="活动说明">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入活动说明"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type === 1"
          label="投票选项"
          prop="options"
        >
          <div class="option-editor">
            <div
              v-for="(option, index) in formData.options"
              :key="index"
              class="option-row"
            >
              <el-input
                v-model="formData.options[index]"
                :placeholder="`请输入选项${index + 1}`"
              />
              <el-button
                v-if="formData.options.length > 2"
                type="danger"
                link
                @click="removeOption(index)"
              >
                删除
              </el-button>
            </div>
            <el-button type="primary" link @click="addOption">
              新增选项
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          发布
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="活动详情" width="760px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="活动标题">
          {{ detailData.title }}
        </el-descriptions-item>
        <el-descriptions-item label="活动类型">
          <el-tag :type="detailData.type === 1 ? 'danger' : 'success'">
            {{ getTypeName(detailData.type) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="活动状态">
          <el-tag :type="detailData.status === 1 ? 'warning' : 'info'">
            {{ getStatusName(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布人">
          {{ detailData.publisherName || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="结束时间">
          {{ detailData.endTime || "不限" }}
        </el-descriptions-item>
        <el-descriptions-item label="参与人数">
          {{ detailData.responseCount || 0 }}
        </el-descriptions-item>
        <el-descriptions-item label="活动说明" :span="2">
          {{ detailData.description || "无" }}
        </el-descriptions-item>
      </el-descriptions>

      <div v-if="detailData.type === 1" class="mt-20">
        <div class="section-title">投票结果</div>
        <template v-if="detailData.optionStats?.length">
          <div
            v-for="item in detailData.optionStats"
            :key="item.optionName"
            class="result-item"
          >
            <div class="result-head">
              <span>{{ item.optionName }}</span>
              <span>{{ item.count }}票 / {{ item.percentage }}%</span>
            </div>
            <el-progress :percentage="item.percentage || 0" :stroke-width="14" />
          </div>
        </template>
        <el-empty v-else description="当前结果仅在已参与或活动结束后可见" />
      </div>

      <div v-if="detailData.type === 2" class="mt-20">
        <div class="section-title">征集反馈</div>
        <template v-if="isManager">
          <el-table
            :data="detailData.responses || []"
            max-height="280"
            style="width: 100%"
          >
            <el-table-column prop="userName" label="业主" width="140" />
            <el-table-column prop="content" label="反馈内容" min-width="260" show-overflow-tooltip />
            <el-table-column prop="createTime" label="提交时间" width="180" />
          </el-table>
        </template>
        <template v-else>
          <el-alert
            :title="detailData.myContent || '您暂未提交意见'"
            :type="detailData.myContent ? 'success' : 'info'"
            :closable="false"
            show-icon
          />
        </template>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="submitDialogVisible"
      :title="submitTarget.type === 1 ? '参与投票' : '提交意见'"
      width="560px"
      @closed="resetSubmitForm"
    >
      <el-form
        ref="submitFormRef"
        :model="submitForm"
        :rules="submitRules"
        label-width="90px"
      >
        <el-form-item label="活动标题">
          <span>{{ submitTarget.title }}</span>
        </el-form-item>
        <el-form-item v-if="submitTarget.type === 1" label="投票选项" prop="selectedOption">
          <el-radio-group v-model="submitForm.selectedOption">
            <el-radio
              v-for="item in submitTarget.options || []"
              :key="item"
              :value="item"
            >
              {{ item }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-else label="征集意见" prop="content">
          <el-input
            v-model="submitForm.content"
            type="textarea"
            :rows="5"
            placeholder="请输入您的意见或建议"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="submitDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="handleSubmitVote">
          提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/stores/user";
import {
  closeVote,
  createVoteActivity,
  deleteVote,
  getVoteById,
  getVotePage,
  submitVote,
} from "@/api/vote";

const userStore = useUserStore();
const isManager = computed(
  () => userStore.userInfo?.role === 1 || userStore.userInfo?.role === 2
);
const isOwner = computed(() => userStore.userInfo?.role === 3);

const loading = ref(false);
const submitLoading = ref(false);
const actionLoading = ref(false);
const total = ref(0);
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const submitDialogVisible = ref(false);
const formRef = ref(null);
const submitFormRef = ref(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: "",
  type: null,
  status: null,
});

const tableData = ref([]);
const detailData = ref({});
const submitTarget = ref({});

const formData = reactive({
  title: "",
  description: "",
  type: 1,
  endTime: null,
  options: ["", ""],
});

const submitForm = reactive({
  selectedOption: "",
  content: "",
});

const validateOptions = (_, value, callback) => {
  if (formData.type !== 1) {
    callback();
    return;
  }
  const validOptions = (value || []).map((item) => item.trim()).filter(Boolean);
  if (validOptions.length < 2) {
    callback(new Error("至少填写两个有效选项"));
    return;
  }
  callback();
};

const formRules = {
  title: [{ required: true, message: "请输入活动标题", trigger: "blur" }],
  type: [{ required: true, message: "请选择活动类型", trigger: "change" }],
  options: [{ validator: validateOptions, trigger: "blur" }],
};

const submitRules = {
  selectedOption: [
    { required: true, message: "请选择投票选项", trigger: "change" },
  ],
  content: [{ required: true, message: "请输入征集意见", trigger: "blur" }],
};

const getTypeName = (type) => {
  const names = { 1: "业主投票", 2: "意见征集" };
  return names[type] || "未知";
};

const getStatusName = (status) => {
  const names = { 1: "进行中", 2: "已结束" };
  return names[status] || "未知";
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getVotePage(queryParams);
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
  queryParams.title = "";
  queryParams.type = null;
  queryParams.status = null;
  handleSearch();
};

const handleAdd = () => {
  resetForm();
  dialogVisible.value = true;
};

const addOption = () => {
  formData.options.push("");
};

const removeOption = (index) => {
  formData.options.splice(index, 1);
};

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  submitLoading.value = true;
  try {
    const payload = {
      title: formData.title,
      description: formData.description,
      type: formData.type,
      endTime: formData.endTime,
      options: formData.type === 1 ? formData.options : [],
    };
    await createVoteActivity(payload);
    ElMessage.success("发布成功");
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
    const res = await getVoteById(row.id);
    detailData.value = res.data;
    detailDialogVisible.value = true;
  } catch (error) {
    console.error(error);
  }
};

const handleParticipate = async (row) => {
  try {
    const res = await getVoteById(row.id);
    submitTarget.value = res.data;
    resetSubmitForm();
    submitDialogVisible.value = true;
  } catch (error) {
    console.error(error);
  }
};

const handleSubmitVote = async () => {
  const valid = await submitFormRef.value.validate().catch(() => false);
  if (!valid) return;

  actionLoading.value = true;
  try {
    await submitVote(submitTarget.value.id, submitForm);
    ElMessage.success("提交成功");
    submitDialogVisible.value = false;
    fetchData();
  } catch (error) {
    console.error(error);
  } finally {
    actionLoading.value = false;
  }
};

const handleClose = (row) => {
  ElMessageBox.confirm("确定要结束该活动吗？结束后业主将无法继续参与。", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await closeVote(row.id);
        ElMessage.success("活动已结束");
        fetchData();
      } catch (error) {
        console.error(error);
      }
    })
    .catch(() => {});
};

const handleDelete = (row) => {
  ElMessageBox.confirm("确定要删除该活动吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteVote(row.id);
        ElMessage.success("删除成功");
        fetchData();
      } catch (error) {
        console.error(error);
      }
    })
    .catch(() => {});
};

const resetForm = () => {
  formData.title = "";
  formData.description = "";
  formData.type = 1;
  formData.endTime = null;
  formData.options = ["", ""];
  formRef.value?.clearValidate();
};

const resetSubmitForm = () => {
  submitForm.selectedOption = "";
  submitForm.content = "";
  submitFormRef.value?.clearValidate();
};

onMounted(() => {
  fetchData();
});
</script>

<style lang="scss" scoped>
.option-editor {
  width: 100%;

  .option-row {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 10px;
  }
}

.section-title {
  margin-bottom: 12px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.result-item {
  margin-bottom: 16px;

  .result-head {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
    color: #606266;
  }
}
</style>
