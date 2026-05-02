<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>清洁管理</span>
      </template>
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            style="width: 120px"
            clearable
          >
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">新增清洁任务</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{
              getStatusName(row.status)
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cleanerName" label="清洁人员" />
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)"
              >查看</el-button
            >
            <el-button
              v-if="row.status === 0"
              type="success"
              link
              @click="handleAssign(row)"
              >指派</el-button
            >
            <el-button
              v-if="row.status !== 2"
              type="warning"
              link
              @click="handleComplete(row)"
              >完成</el-button
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

    <!-- 新增清洁任务对话框 -->
    <el-dialog v-model="dialogVisible" title="新增清洁任务" width="640px">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="位置" prop="location">
          <el-input v-model="formData.location" placeholder="请输入清洁位置" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入清洁描述"
          />
        </el-form-item>
        <el-form-item label="照片">
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

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="清洁任务详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="位置">{{
          viewData.location
        }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(viewData.status)">{{
            getStatusName(viewData.status)
          }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="描述">{{
          viewData.description || "无"
        }}</el-descriptions-item>
        <el-descriptions-item label="照片">
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
        <el-descriptions-item label="提交时间">{{
          viewData.createTime
        }}</el-descriptions-item>
        <el-descriptions-item label="清洁人员" v-if="viewData.cleanerName">{{
          viewData.cleanerName
        }}</el-descriptions-item>
        <el-descriptions-item label="清洁结果" v-if="viewData.cleanResult">{{
          viewData.cleanResult
        }}</el-descriptions-item>
        <el-descriptions-item label="清洁时间" v-if="viewData.cleanTime">{{
          viewData.cleanTime
        }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="imagePreviewVisible" title="图片预览" width="640px">
      <img :src="imagePreviewUrl" class="preview-image" alt="预览图片" />
    </el-dialog>

    <!-- 指派清洁人员对话框 -->
    <el-dialog v-model="assignDialogVisible" title="指派清洁人员" width="480px">
      <el-form label-width="80px">
        <el-form-item label="清洁人员">
          <el-select
            v-model="assignForm.cleanerId"
            placeholder="请选择清洁人员"
            style="width: 100%"
          >
            <el-option
              v-for="item in staffList"
              :key="item.id"
              :label="item.realName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleAssignSubmit"
          :loading="assignLoading"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 完成清洁任务对话框 -->
    <el-dialog
      v-model="completeDialogVisible"
      title="完成清洁任务"
      width="480px"
    >
      <el-form label-width="80px">
        <el-form-item label="清洁结果">
          <el-input
            v-model="completeForm.cleanResult"
            type="textarea"
            :rows="4"
            placeholder="请输入清洁结果"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleCompleteSubmit"
          :loading="completeLoading"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import {
  createCleaning,
  getCleaningPage,
  getCleaningById,
  deleteCleaning,
  assignCleaning,
  completeCleaning,
} from "@/api/cleaning";
import { uploadImage } from "@/api/file";
import { getStaffList } from "@/api/user";

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

const assignDialogVisible = ref(false);
const assignLoading = ref(false);
const staffList = ref([]);
const assignForm = reactive({ cleanerId: null });
const assignTargetId = ref(null);

const completeDialogVisible = ref(false);
const completeLoading = ref(false);
const completeForm = reactive({ cleanResult: "" });
const completeTargetId = ref(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: null,
});

const tableData = ref([]);

const formData = reactive({
  location: "",
  description: "",
  images: "",
});

const formRules = {
  location: [{ required: true, message: "请输入清洁位置", trigger: "blur" }],
};

const viewData = ref({});

const getStatusType = (status) => {
  const types = { 0: "warning", 1: "primary", 2: "success" };
  return types[status] || "info";
};

const getStatusName = (status) => {
  const names = { 0: "待处理", 1: "处理中", 2: "已完成" };
  return names[status] || "未知";
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getCleaningPage(queryParams);
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
  handleSearch();
};

const handleAdd = () => {
  formData.location = "";
  formData.description = "";
  formData.images = "";
  imageUploadFiles.value = [];
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
    await createCleaning({ ...formData });
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
    const res = await getCleaningById(row.id);
    viewData.value = res.data;
    viewDialogVisible.value = true;
  } catch (error) {
    console.error(error);
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm("确定要删除该清洁任务吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteCleaning(row.id);
        ElMessage.success("删除成功");
        fetchData();
      } catch (error) {
        console.error(error);
      }
    })
    .catch(() => {});
};

const handleAssign = async (row) => {
  assignTargetId.value = row.id;
  assignForm.cleanerId = null;
  try {
    const res = await getStaffList();
    staffList.value = res.data;
  } catch (error) {
    console.error(error);
  }
  assignDialogVisible.value = true;
};

const handleAssignSubmit = async () => {
  if (!assignForm.cleanerId) {
    ElMessage.warning("请选择清洁人员");
    return;
  }
  assignLoading.value = true;
  try {
    await assignCleaning(assignTargetId.value, { cleanerId: assignForm.cleanerId });
    ElMessage.success("指派成功");
    assignDialogVisible.value = false;
    fetchData();
  } catch (error) {
    console.error(error);
  } finally {
    assignLoading.value = false;
  }
};

const handleComplete = (row) => {
  completeTargetId.value = row.id;
  completeForm.cleanResult = "";
  completeDialogVisible.value = true;
};

const handleCompleteSubmit = async () => {
  completeLoading.value = true;
  try {
    await completeCleaning(completeTargetId.value, { cleanResult: completeForm.cleanResult });
    ElMessage.success("已完成");
    completeDialogVisible.value = false;
    fetchData();
  } catch (error) {
    console.error(error);
  } finally {
    completeLoading.value = false;
  }
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
