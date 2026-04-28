<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>访客管理</span>
      </template>

      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item :label="isManager ? '访客/通行证' : '访客姓名'">
          <el-input
            v-model="queryParams.keyword"
            :placeholder="isManager ? '请输入访客姓名/手机号/通行证' : '请输入访客姓名'"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="待通行" :value="0" />
            <el-option label="已通行" :value="1" />
            <el-option label="已失效" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="isOwner" type="success" @click="handleAdd">
            新增登记
          </el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="visitorName" label="访客姓名" width="120" />
        <el-table-column prop="visitorPhone" label="手机号" width="140" />
        <el-table-column prop="visitorCount" label="人数" width="80" />
        <el-table-column prop="purpose" label="来访事由" min-width="180" show-overflow-tooltip />
        <el-table-column v-if="isManager" prop="ownerName" label="被访业主" width="120" />
        <el-table-column prop="passCode" label="通行证" width="120">
          <template #default="{ row }">
            <el-tag type="success" effect="plain">{{ row.passCode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="visitTime" label="到访时间" width="180" />
        <el-table-column prop="validUntil" label="有效截止" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button
              v-if="isManager && row.status === 0"
              type="success"
              link
              @click="handleVerify(row)"
            >
              核销
            </el-button>
            <el-button
              v-if="(isOwner || isManager) && row.status === 0"
              type="danger"
              link
              @click="handleCancel(row)"
            >
              取消
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
      title="访客登记"
      width="560px"
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="90px"
      >
        <el-form-item label="访客姓名" prop="visitorName">
          <el-input v-model="formData.visitorName" placeholder="请输入访客姓名" />
        </el-form-item>
        <el-form-item label="访客手机号" prop="visitorPhone">
          <el-input v-model="formData.visitorPhone" placeholder="请输入访客手机号" />
        </el-form-item>
        <el-form-item label="来访人数" prop="visitorCount">
          <el-input-number v-model="formData.visitorCount" :min="1" :max="20" />
        </el-form-item>
        <el-form-item label="到访时间" prop="visitTime">
          <el-date-picker
            v-model="formData.visitTime"
            type="datetime"
            placeholder="请选择到访时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="有效截止">
          <el-date-picker
            v-model="formData.validUntil"
            type="datetime"
            placeholder="不填默认到访后6小时"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="车牌号">
          <el-input v-model="formData.licensePlate" placeholder="请输入车牌号，可选" />
        </el-form-item>
        <el-form-item label="来访事由" prop="purpose">
          <el-input
            v-model="formData.purpose"
            type="textarea"
            :rows="3"
            placeholder="请输入来访事由"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注，可选"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          提交
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="访客详情" width="640px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="访客姓名">
          {{ detailData.visitorName }}
        </el-descriptions-item>
        <el-descriptions-item label="访客手机号">
          {{ detailData.visitorPhone }}
        </el-descriptions-item>
        <el-descriptions-item label="来访人数">
          {{ detailData.visitorCount }}
        </el-descriptions-item>
        <el-descriptions-item label="被访业主">
          {{ detailData.ownerName || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="通行证编号">
          <el-tag type="success">{{ detailData.passCode }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="当前状态">
          <el-tag :type="getStatusType(detailData.status)">
            {{ getStatusName(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="到访时间">
          {{ detailData.visitTime }}
        </el-descriptions-item>
        <el-descriptions-item label="有效截止">
          {{ detailData.validUntil }}
        </el-descriptions-item>
        <el-descriptions-item label="车牌号">
          {{ detailData.licensePlate || "无" }}
        </el-descriptions-item>
        <el-descriptions-item label="核销人">
          {{ detailData.verifierName || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="核销时间">
          {{ detailData.verifyTime || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ detailData.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="来访事由" :span="2">
          {{ detailData.purpose || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ detailData.remark || "无" }}
        </el-descriptions-item>
      </el-descriptions>

      <div class="pass-card mt-20">
        <div class="pass-card__label">访客通行证</div>
        <div class="pass-card__code">{{ detailData.passCode }}</div>
        <div class="pass-card__meta">
          <span>{{ detailData.visitorName }}</span>
          <span>{{ detailData.visitTime }}</span>
        </div>
      </div>

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
  cancelVisitor,
  createVisitor,
  getVisitorById,
  getVisitorPage,
  verifyVisitor,
} from "@/api/visitor";

const userStore = useUserStore();
const isManager = computed(
  () => userStore.userInfo?.role === 1 || userStore.userInfo?.role === 2
);
const isOwner = computed(() => userStore.userInfo?.role === 3);

const loading = ref(false);
const submitLoading = ref(false);
const total = ref(0);
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const formRef = ref(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: "",
  status: null,
});

const tableData = ref([]);
const detailData = ref({});

const formData = reactive({
  visitorName: "",
  visitorPhone: "",
  visitorCount: 1,
  licensePlate: "",
  purpose: "",
  visitTime: null,
  validUntil: null,
  remark: "",
});

const formRules = {
  visitorName: [{ required: true, message: "请输入访客姓名", trigger: "blur" }],
  visitorPhone: [{ required: true, message: "请输入访客手机号", trigger: "blur" }],
  visitorCount: [{ required: true, message: "请输入来访人数", trigger: "change" }],
  visitTime: [{ required: true, message: "请选择到访时间", trigger: "change" }],
  purpose: [{ required: true, message: "请输入来访事由", trigger: "blur" }],
};

const getStatusType = (status) => {
  const types = { 0: "warning", 1: "success", 2: "info", 3: "danger" };
  return types[status] || "info";
};

const getStatusName = (status) => {
  const names = { 0: "待通行", 1: "已通行", 2: "已失效", 3: "已取消" };
  return names[status] || "未知";
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getVisitorPage(queryParams);
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
  queryParams.keyword = "";
  queryParams.status = null;
  handleSearch();
};

const handleAdd = () => {
  resetForm();
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  submitLoading.value = true;
  try {
    await createVisitor(formData);
    ElMessage.success("登记成功");
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
    const res = await getVisitorById(row.id);
    detailData.value = res.data;
    detailDialogVisible.value = true;
  } catch (error) {
    console.error(error);
  }
};

const handleVerify = (row) => {
  ElMessageBox.confirm("确定核销该通行证吗？核销后将标记为已通行。", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await verifyVisitor(row.id);
        ElMessage.success("核销成功");
        fetchData();
      } catch (error) {
        console.error(error);
      }
    })
    .catch(() => {});
};

const handleCancel = (row) => {
  ElMessageBox.confirm("确定取消该访客登记吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await cancelVisitor(row.id);
        ElMessage.success("已取消");
        fetchData();
      } catch (error) {
        console.error(error);
      }
    })
    .catch(() => {});
};

const resetForm = () => {
  formData.visitorName = "";
  formData.visitorPhone = "";
  formData.visitorCount = 1;
  formData.licensePlate = "";
  formData.purpose = "";
  formData.visitTime = null;
  formData.validUntil = null;
  formData.remark = "";
  formRef.value?.clearValidate();
};

onMounted(() => {
  fetchData();
});
</script>

<style lang="scss" scoped>
.pass-card {
  padding: 18px 20px;
  border-radius: 16px;
  color: #fff;
  background: linear-gradient(135deg, #2d6cdf 0%, #46a0ff 100%);

  .pass-card__label {
    font-size: 13px;
    opacity: 0.85;
  }

  .pass-card__code {
    margin: 10px 0 14px;
    font-size: 28px;
    font-weight: 700;
    letter-spacing: 4px;
  }

  .pass-card__meta {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    font-size: 13px;
    opacity: 0.9;
  }
}
</style>
