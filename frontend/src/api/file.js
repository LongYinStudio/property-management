import request from "@/utils/request";

// 上传业务图片
export function uploadImage(file) {
  const formData = new FormData();
  formData.append("file", file);

  return request({
    url: "/file/image",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}
