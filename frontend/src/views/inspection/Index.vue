<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>设备巡检</span>
      </template>
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="设备类型">
          <el-select
            v-model="queryParams.equipmentType"
            placeholder="请选择类型"
            style="width: 140px"
            clearable
          >
            <el-option
              v-for="item in equipmentTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="巡检状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            style="width: 120px"
            clearable
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">新增巡检记录</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="equipmentName" label="设备名称" min-width="140" />
        <el-table-column prop="equipmentType" label="设备类型" width="120">
          <template #default="{ row }">
            {{ getEquipmentTypeName(row.equipmentType) }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" min-width="160" />
        <el-table-column prop="inspectionDate" label="巡检日期" width="120" />
        <el-table-column
          prop="nextInspectionDate"
          label="下次巡检"
          width="120"
        >
          <template #default="{ row }">
            {{ row.nextInspectionDate || "未设置" }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="巡检人" width="120">
          <template #default="{ row }">
            {{ row.userName || "未知" }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">
              查看
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
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

    <el-dialog v-model="dialogVisible" title="新增巡检记录" width="680px">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="110px"
      >
        <el-form-item label="设备名称" prop="equipmentName">
          <el-input
            v-model="formData.equipmentName"
            placeholder="请输入设备名称"
          />
        </el-form-item>
        <el-form-item label="设备类型" prop="equipmentType">
          <el-select
            v-model="formData.equipmentType"
            placeholder="请选择设备类型"
            style="width: 100%"
          >
            <el-option
              v-for="item in equipmentTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="设备位置" prop="location">
          <el-input v-model="formData.location" placeholder="请输入设备位置" />
        </el-form-item>
        <el-form-item label="巡检日期" prop="inspectionDate">
          <el-date-picker
            v-model="formData.inspectionDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择巡检日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="下次巡检">
          <el-date-picker
            v-model="formData.nextInspectionDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择下次巡检日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="巡检状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">异常</el-radio>
            <el-radio :label="2">已维修</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="巡检结果" prop="result">
          <el-input
            v-model="formData.result"
            type="textarea"
            :rows="3"
            placeholder="请输入巡检结果"
          />
        </el-form-item>
        <el-form-item label="问题描述">
          <el-input
            v-model="formData.issueDescription"
            type="textarea"
            :rows="3"
            placeholder="如存在异常，请填写问题描述"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
        <el-form-item label="现场照片">
          <el-upload
            v-model:file-list="imageUploadFiles"
            action="#"
            list-type="picture-card"
            :limit="3"
            :before-upload="beforeImageUpload"
            :http-request="handleImageUpload"
            :on-remove="handleImageRemove"
            :on-preview="handleImagePreview"
            :on-exceed="handleImageExceed"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">最多上传 3 张图片，单张不超过 5MB</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleSubmit"
          :loading="submitLoading"
          :disabled="imageUploading"
        >
          提交
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="viewDialogVisible" title="巡检记录详情" width="560px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="设备名称">
          {{ viewData.equipmentName }}
        </el-descriptions-item>
        <el-descriptions-item label="设备类型">
          {{ getEquipmentTypeName(viewData.equipmentType) }}
        </el-descriptions-item>
        <el-descriptions-item label="设备位置">
          {{ viewData.location }}
        </el-descriptions-item>
        <el-descriptions-item label="巡检状态">
          <el-tag :type="getStatusType(viewData.status)">
            {{ getStatusName(viewData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="巡检日期">
          {{ viewData.inspectionDate }}
        </el-descriptions-item>
        <el-descriptions-item label="下次巡检">
          {{ viewData.nextInspectionDate || "未设置" }}
        </el-descriptions-item>
        <el-descriptions-item label="巡检人">
          {{ viewData.userName || "未知" }}
        </el-descriptions-item>
        <el-descriptions-item label="巡检结果">
          {{ viewData.result || "无" }}
        </el-descriptions-item>
        <el-descriptions-item label="问题描述">
          {{ viewData.issueDescription || "无" }}
        </el-descriptions-item>
        <el-descriptions-item label="现场照片">
          <div v-if="parseImages(viewData.images).length" class="image-list">
            <el-image
              v-for="(image, index) in parseImages(viewData.images)"
              :key="image"
              :src="image"
              :preview-src-list="parseImages(viewData.images)"
              :initial-index="index"
              fit="cover"
              class="detail-image"
            />
          </div>
          <span v-else>无</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注">
          {{ viewData.remark || "无" }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ viewData.createTime }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="imagePreviewVisible" title="图片预览" width="640px">
      <img :src="imagePreviewUrl" class="preview-image" alt="预览图片" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import {
  createInspection,
  getInspectionPage,
  getInspectionById,
  deleteInspection,
} from "@/api/inspection";
import { uploadImage } from "@/api/file";

const loading = ref(false);
const total = ref(0);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const viewDialogVisible = ref(false);
const formRef = ref(null);
const imageUploadFiles = ref([]);
const imageUploading = ref(false);
const imageUploadCount = ref(0);
const imagePreviewVisible = ref(false);
const imagePreviewUrl = ref("");

const equipmentTypeOptions = [
  { label: "消防设施", value: 1 },
  { label: "电梯", value: 2 },
  { label: "监控", value: 3 },
  { label: "给排水", value: 4 },
  { label: "配电", value: 5 },
  { label: "其他", value: 6 },
];

const statusOptions = [
  { label: "正常", value: 0 },
  { label: "异常", value: 1 },
  { label: "已维修", value: 2 },
];

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: null,
  equipmentType: null,
});

const tableData = ref([]);

const formData = reactive({
  equipmentName: "",
  equipmentType: null,
  location: "",
  inspectionDate: "",
  nextInspectionDate: "",
  status: 0,
  result: "",
  issueDescription: "",
  images: "",
  remark: "",
});

const formRules = {
  equipmentName: [
    { required: true, message: "请输入设备名称", trigger: "blur" },
  ],
  equipmentType: [
    { required: true, message: "请选择设备类型", trigger: "change" },
  ],
  location: [{ required: true, message: "请输入设备位置", trigger: "blur" }],
  inspectionDate: [
    { required: true, message: "请选择巡检日期", trigger: "change" },
  ],
  status: [{ required: true, message: "请选择巡检状态", trigger: "change" }],
  result: [{ required: true, message: "请输入巡检结果", trigger: "blur" }],
};

const viewData = ref({});

const getEquipmentTypeName = (type) => {
  return equipmentTypeOptions.find((item) => item.value === type)?.label || "未知";
};

const getStatusType = (status) => {
  const types = { 0: "success", 1: "danger", 2: "warning" };
  return types[status] || "info";
};

const getStatusName = (status) => {
  return statusOptions.find((item) => item.value === status)?.label || "未知";
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getInspectionPage(queryParams);
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
  queryParams.status = null;
  queryParams.equipmentType = null;
  handleSearch();
};

const resetForm = () => {
  formData.equipmentName = "";
  formData.equipmentType = null;
  formData.location = "";
  formData.inspectionDate = "";
  formData.nextInspectionDate = "";
  formData.status = 0;
  formData.result = "";
  formData.issueDescription = "";
  formData.images = "";
  formData.remark = "";
  imageUploadFiles.value = [];
};

const handleAdd = () => {
  resetForm();
  dialogVisible.value = true;
};

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith("image/");
  const isLt5M = file.size / 1024 / 1024 <= 5;

  if (!isImage) {
    ElMessage.error("只能上传图片文件");
    return false;
  }
  if (!isLt5M) {
    ElMessage.error("图片大小不能超过 5MB");
    return false;
  }
  return true;
};

const updateImages = () => {
  formData.images = imageUploadFiles.value
    .map((file) => file.url)
    .filter(Boolean)
    .join(",");
};

const handleImageUpload = async (options) => {
  imageUploadCount.value += 1;
  imageUploading.value = true;

  try {
    const res = await uploadImage(options.file);
    const currentFile = imageUploadFiles.value.find(
      (file) => file.uid === options.file.uid,
    );
    if (currentFile) {
      currentFile.url = res.data.url;
      currentFile.status = "success";
    }
    updateImages();
    options.onSuccess?.(res.data);
  } catch (error) {
    imageUploadFiles.value = imageUploadFiles.value.filter(
      (file) => file.uid !== options.file.uid,
    );
    updateImages();
    options.onError?.(error);
  } finally {
    imageUploadCount.value -= 1;
    imageUploading.value = imageUploadCount.value > 0;
  }
};

const handleImageRemove = () => {
  updateImages();
};

const handleImagePreview = (file) => {
  imagePreviewUrl.value = file.url;
  imagePreviewVisible.value = true;
};

const handleImageExceed = () => {
  ElMessage.warning("最多上传 3 张图片");
};

const parseImages = (images) => {
  if (!images) return [];
  return images
    .split(",")
    .map((image) => image.trim())
    .filter(Boolean);
};

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;
  if (imageUploading.value) {
    ElMessage.warning("图片正在上传，请稍后提交");
    return;
  }

  submitLoading.value = true;
  try {
    await createInspection({ ...formData });
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
    const res = await getInspectionById(row.id);
    viewData.value = res.data;
    viewDialogVisible.value = true;
  } catch (error) {
    console.error(error);
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm("确定要删除该巡检记录吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteInspection(row.id);
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

.upload-tip {
  width: 100%;
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
  line-height: 18px;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-image {
  width: 88px;
  height: 88px;
  border-radius: 4px;
}

.preview-image {
  display: block;
  width: 100%;
  max-height: 70vh;
  object-fit: contain;
}
</style>
