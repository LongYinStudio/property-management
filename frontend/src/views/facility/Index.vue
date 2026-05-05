<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>设备台账</span>
      </template>

      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="关键字">
          <el-input
            v-model="queryParams.keyword"
            placeholder="设施名称 / 位置"
            clearable
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-select
            v-model="queryParams.type"
            placeholder="请选择类型"
            clearable
            style="width: 160px"
          >
            <el-option
              v-for="item in typeOptions"
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
            <el-option label="正常" :value="1" />
            <el-option label="故障" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="isManager" type="success" @click="handleAdd">新增设施</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="设施名称" min-width="180" />
        <el-table-column prop="typeName" label="类型" width="120">
          <template #default="{ row }">
            {{ row.typeName }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.location || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastCheckDate" label="上次检查" width="120">
          <template #default="{ row }">
            {{ row.lastCheckDate || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="nextCheckDate" label="下次检查" width="120">
          <template #default="{ row }">
            {{ row.nextCheckDate || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button v-if="isManager" type="success" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="isManager" type="danger" link @click="handleDelete(row)">删除</el-button>
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
      :title="dialogMode === 'create' ? '新增设施' : '编辑设施'"
      width="700px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="96px">
        <div class="form-grid">
          <el-form-item label="设施名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入设施名称" />
          </el-form-item>
          <el-form-item label="设施类型" prop="type">
            <el-select v-model="formData.type" placeholder="请选择设施类型" style="width: 100%">
              <el-option
                v-for="item in typeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="设施位置" prop="location" class="form-grid__full">
            <el-input v-model="formData.location" placeholder="请输入设施所在位置" />
          </el-form-item>
          <el-form-item label="设施状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio :label="1">正常</el-radio>
              <el-radio :label="0">故障</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="上次检查">
            <el-date-picker
              v-model="formData.lastCheckDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择日期"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="下次检查">
            <el-date-picker
              v-model="formData.nextCheckDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择日期"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入设施备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="设施详情" width="760px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="设施名称">{{ detailData.name || "-" }}</el-descriptions-item>
        <el-descriptions-item label="设施类型">{{ detailData.typeName || "-" }}</el-descriptions-item>
        <el-descriptions-item label="设施位置" :span="2">
          {{ detailData.location || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="设施状态">
          <el-tag :type="detailData.status === 1 ? 'success' : 'danger'">
            {{ detailData.statusName || "-" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="上次检查">{{ detailData.lastCheckDate || "-" }}</el-descriptions-item>
        <el-descriptions-item label="下次检查">{{ detailData.nextCheckDate || "-" }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime || "-" }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime || "-" }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          <div class="remark-content">{{ detailData.remark || "-" }}</div>
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
  createFacility,
  updateFacility,
  getFacilityPage,
  getFacilityById,
  deleteFacility,
} from "@/api/facility";

const userStore = useUserStore();

const loading = ref(false);
const total = ref(0);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const dialogMode = ref("create");
const currentId = ref(null);
const formRef = ref(null);
const tableData = ref([]);
const detailData = ref({});

const isManager = computed(() => userStore.userInfo?.role === 1 || userStore.userInfo?.role === 2);

const typeOptions = [
  { label: "消防设施", value: 1 },
  { label: "电梯", value: 2 },
  { label: "监控", value: 3 },
  { label: "其他", value: 4 },
];

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: "",
  type: null,
  status: null,
});

const createDefaultFormData = () => ({
  name: "",
  type: null,
  location: "",
  status: 1,
  lastCheckDate: "",
  nextCheckDate: "",
  remark: "",
});

const formData = reactive(createDefaultFormData());

const formRules = {
  name: [{ required: true, message: "请输入设施名称", trigger: "blur" }],
  type: [{ required: true, message: "请选择设施类型", trigger: "change" }],
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getFacilityPage(queryParams);
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
  queryParams.keyword = "";
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
  const res = await getFacilityById(row.id);
  Object.assign(formData, {
    name: res.data.name,
    type: res.data.type,
    location: res.data.location,
    status: res.data.status,
    lastCheckDate: res.data.lastCheckDate,
    nextCheckDate: res.data.nextCheckDate,
    remark: res.data.remark,
  });
  formRef.value?.clearValidate();
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  await formRef.value.validate();
  submitLoading.value = true;
  try {
    if (dialogMode.value === "create") {
      await createFacility(formData);
      ElMessage.success("设施创建成功");
    } else {
      await updateFacility(currentId.value, formData);
      ElMessage.success("设施更新成功");
    }
    dialogVisible.value = false;
    fetchData();
  } finally {
    submitLoading.value = false;
  }
};

const handleView = async (row) => {
  const res = await getFacilityById(row.id);
  detailData.value = res.data;
  detailDialogVisible.value = true;
};

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除设施“${row.name}”吗？`, "提示", {
    type: "warning",
  });
  await deleteFacility(row.id);
  ElMessage.success("设施删除成功");
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
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;

  &__full {
    grid-column: 1 / -1;
  }
}

.remark-content {
  white-space: pre-wrap;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;

    &__full {
      grid-column: auto;
    }
  }
}
</style>
