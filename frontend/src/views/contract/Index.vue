<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>业主合同电子化管理</span>
          <span class="header-tip">集中管理合同台账、附件归档与线上查阅</span>
        </div>
      </template>

      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="queryParams.keyword"
            placeholder="合同编号/名称/房屋信息"
            clearable
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item label="合同类型">
          <el-select
            v-model="queryParams.contractType"
            placeholder="请选择类型"
            clearable
            style="width: 140px"
          >
            <el-option
              v-for="item in contractTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 140px"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="业主" v-if="isManager">
          <el-select
            v-model="queryParams.userId"
            placeholder="请选择业主"
            clearable
            filterable
            style="width: 180px"
          >
            <el-option
              v-for="user in ownerOptions"
              :key="user.id"
              :label="user.realName"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd" v-if="isManager">
            新增合同
          </el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="contractNo" label="合同编号" width="160" />
        <el-table-column prop="contractName" label="合同名称" min-width="180" />
        <el-table-column prop="userName" label="业主" width="120" />
        <el-table-column prop="roomInfo" label="房屋信息" min-width="150" show-overflow-tooltip />
        <el-table-column prop="contractType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag type="info">{{ getContractTypeLabel(row.contractType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="合同金额" width="120">
          <template #default="{ row }">
            {{ row.amount ? `¥${row.amount}` : "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="signDate" label="签署日期" width="120" />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="attachmentName" label="电子附件" min-width="170" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link
              v-if="row.attachmentUrl"
              type="primary"
              @click="openAttachment(row.attachmentUrl)"
            >
              {{ row.attachmentName || "查看附件" }}
            </el-link>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="录入时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="warning" link @click="handleEdit(row)" v-if="isManager">
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)" v-if="isManager">
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="720px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="业主" prop="userId">
              <el-select v-model="formData.userId" placeholder="请选择业主" filterable>
                <el-option
                  v-for="user in ownerOptions"
                  :key="user.id"
                  :label="user.realName"
                  :value="user.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同编号" prop="contractNo">
              <el-input v-model="formData.contractNo" placeholder="请输入合同编号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="合同名称" prop="contractName">
              <el-input v-model="formData.contractName" placeholder="请输入合同名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同类型" prop="contractType">
              <el-select v-model="formData.contractType" placeholder="请选择合同类型">
                <el-option
                  v-for="item in contractTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="签署日期" prop="signDate">
              <el-date-picker
                v-model="formData.signDate"
                type="date"
                placeholder="请选择签署日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态">
                <el-option
                  v-for="item in statusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="formData.startDate"
                type="date"
                placeholder="请选择开始日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="formData.endDate"
                type="date"
                placeholder="请选择结束日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="合同金额" prop="amount">
              <el-input-number
                v-model="formData.amount"
                :min="0"
                :precision="2"
                :controls="false"
                style="width: 100%"
                placeholder="请输入合同金额"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房屋信息" prop="roomInfo">
              <el-input v-model="formData.roomInfo" placeholder="如 1栋2单元1203" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="合同附件">
          <div class="upload-row">
            <el-upload
              :show-file-list="false"
              :http-request="handleUpload"
              accept=".pdf,.doc,.docx,.jpg,.jpeg,.png"
            >
              <el-button type="primary" :loading="uploading">上传附件</el-button>
            </el-upload>
            <div class="upload-info">
              <el-link
                v-if="formData.attachmentUrl"
                type="primary"
                @click="openAttachment(formData.attachmentUrl)"
              >
                {{ formData.attachmentName || "已上传附件" }}
              </el-link>
              <span v-else>支持 PDF、Word、图片，最大 20MB</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
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

    <el-dialog v-model="viewDialogVisible" title="合同详情" width="720px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="合同编号">{{ viewData.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="合同名称">{{ viewData.contractName }}</el-descriptions-item>
        <el-descriptions-item label="业主">{{ viewData.userName || "-" }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ viewData.userPhone || "-" }}</el-descriptions-item>
        <el-descriptions-item label="合同类型">
          {{ getContractTypeLabel(viewData.contractType) }}
        </el-descriptions-item>
        <el-descriptions-item label="合同状态">
          <el-tag :type="getStatusTagType(viewData.status)">
            {{ getStatusLabel(viewData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="签署日期">{{ viewData.signDate || "-" }}</el-descriptions-item>
        <el-descriptions-item label="合同金额">
          {{ viewData.amount ? `¥${viewData.amount}` : "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ viewData.startDate || "-" }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ viewData.endDate || "-" }}</el-descriptions-item>
        <el-descriptions-item label="房屋信息" :span="2">{{ viewData.roomInfo || "-" }}</el-descriptions-item>
        <el-descriptions-item label="电子附件" :span="2">
          <el-link
            v-if="viewData.attachmentUrl"
            type="primary"
            @click="openAttachment(viewData.attachmentUrl)"
          >
            {{ viewData.attachmentName || "查看附件" }}
          </el-link>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark || "-" }}</el-descriptions-item>
        <el-descriptions-item label="录入人">{{ viewData.creatorName || "-" }}</el-descriptions-item>
        <el-descriptions-item label="录入时间">{{ viewData.createTime || "-" }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/stores/user";
import { getOwnerList } from "@/api/user";
import { uploadDocument } from "@/api/file";
import {
  createOwnerContract,
  deleteOwnerContract,
  getOwnerContractById,
  getOwnerContractPage,
  updateOwnerContract,
} from "@/api/ownerContract";

const userStore = useUserStore();
const isManager = computed(
  () => userStore.userInfo?.role === 1 || userStore.userInfo?.role === 2
);

const loading = ref(false);
const submitLoading = ref(false);
const uploading = ref(false);
const total = ref(0);
const dialogVisible = ref(false);
const viewDialogVisible = ref(false);
const dialogTitle = ref("新增合同");
const formRef = ref(null);

const tableData = ref([]);
const ownerOptions = ref([]);
const viewData = ref({});

const contractTypeOptions = [
  { label: "入住协议", value: 1 },
  { label: "装修协议", value: 2 },
  { label: "车位合同", value: 3 },
  { label: "委托服务协议", value: 4 },
  { label: "其他", value: 5 },
];

const statusOptions = [
  { label: "待签署", value: 1 },
  { label: "生效中", value: 2 },
  { label: "已到期", value: 3 },
  { label: "已终止", value: 4 },
];

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: "",
  contractType: undefined,
  status: undefined,
  userId: undefined,
});

const createDefaultFormData = () => ({
  id: undefined,
  userId: undefined,
  contractNo: "",
  contractName: "",
  contractType: 1,
  roomInfo: "",
  signDate: "",
  startDate: "",
  endDate: "",
  amount: undefined,
  status: 1,
  attachmentName: "",
  attachmentUrl: "",
  remark: "",
});

const formData = reactive(createDefaultFormData());

const validateDateRange = (_, __, callback) => {
  if (
    formData.startDate &&
    formData.endDate &&
    new Date(formData.endDate) < new Date(formData.startDate)
  ) {
    callback(new Error("结束日期不能早于开始日期"));
    return;
  }
  callback();
};

const formRules = {
  userId: [{ required: true, message: "请选择业主", trigger: "change" }],
  contractNo: [{ required: true, message: "请输入合同编号", trigger: "blur" }],
  contractName: [{ required: true, message: "请输入合同名称", trigger: "blur" }],
  contractType: [{ required: true, message: "请选择合同类型", trigger: "change" }],
  signDate: [{ required: true, message: "请选择签署日期", trigger: "change" }],
  status: [{ required: true, message: "请选择合同状态", trigger: "change" }],
  startDate: [{ validator: validateDateRange, trigger: "change" }],
  endDate: [{ validator: validateDateRange, trigger: "change" }],
};

const fetchOwnerOptions = async () => {
  if (!isManager.value) {
    return;
  }
  const res = await getOwnerList();
  ownerOptions.value = res.data || [];
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getOwnerContractPage(queryParams);
    tableData.value = res.data.records;
    total.value = res.data.total;
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
  queryParams.keyword = "";
  queryParams.contractType = undefined;
  queryParams.status = undefined;
  queryParams.userId = undefined;
  fetchData();
};

const resetForm = () => {
  Object.assign(formData, createDefaultFormData());
  formRef.value?.clearValidate();
};

const handleAdd = () => {
  resetForm();
  dialogTitle.value = "新增合同";
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  resetForm();
  dialogTitle.value = "编辑合同";
  Object.assign(formData, {
    id: row.id,
    userId: row.userId,
    contractNo: row.contractNo,
    contractName: row.contractName,
    contractType: row.contractType,
    roomInfo: row.roomInfo,
    signDate: row.signDate,
    startDate: row.startDate,
    endDate: row.endDate,
    amount: row.amount,
    status: row.status,
    attachmentName: row.attachmentName,
    attachmentUrl: row.attachmentUrl,
    remark: row.remark,
  });
  dialogVisible.value = true;
};

const handleView = async (row) => {
  const res = await getOwnerContractById(row.id);
  viewData.value = res.data;
  viewDialogVisible.value = true;
};

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除合同“${row.contractName}”吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  });
  await deleteOwnerContract(row.id);
  ElMessage.success("删除成功");
  fetchData();
};

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) {
    return;
  }

  submitLoading.value = true;
  try {
    const payload = {
      userId: formData.userId,
      contractNo: formData.contractNo,
      contractName: formData.contractName,
      contractType: formData.contractType,
      roomInfo: formData.roomInfo,
      signDate: formData.signDate,
      startDate: formData.startDate || null,
      endDate: formData.endDate || null,
      amount: formData.amount,
      status: formData.status,
      attachmentName: formData.attachmentName,
      attachmentUrl: formData.attachmentUrl,
      remark: formData.remark,
    };

    if (formData.id) {
      await updateOwnerContract(formData.id, payload);
      ElMessage.success("更新成功");
    } else {
      await createOwnerContract(payload);
      ElMessage.success("创建成功");
    }

    dialogVisible.value = false;
    fetchData();
  } finally {
    submitLoading.value = false;
  }
};

const handleUpload = async ({ file }) => {
  uploading.value = true;
  try {
    const res = await uploadDocument(file);
    formData.attachmentUrl = res.data.url;
    formData.attachmentName = res.data.name;
    ElMessage.success("附件上传成功");
  } finally {
    uploading.value = false;
  }
};

const openAttachment = (url) => {
  window.open(url, "_blank");
};

const getContractTypeLabel = (value) =>
  contractTypeOptions.find((item) => item.value === value)?.label || "未知";

const getStatusLabel = (value) =>
  statusOptions.find((item) => item.value === value)?.label || "未知";

const getStatusTagType = (value) => {
  switch (value) {
    case 1:
      return "warning";
    case 2:
      return "success";
    case 3:
      return "info";
    case 4:
      return "danger";
    default:
      return "";
  }
};

onMounted(async () => {
  await fetchOwnerOptions();
  await fetchData();
});
</script>

<style lang="scss" scoped>
.page-container {
  .card-header {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    gap: 12px;

    .header-tip {
      font-size: 12px;
      color: #909399;
    }
  }
}

.search-form {
  margin-bottom: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.upload-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;

  .upload-info {
    color: #909399;
    line-height: 1.5;
  }
}
</style>
