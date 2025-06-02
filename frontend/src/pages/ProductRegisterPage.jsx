import React, { useState, useRef, useEffect } from "react";
import axios from "axios";
import jwtDecode from "jwt-decode";
import {
  Box,
  Button,
  Divider,
  IconButton,
  InputAdornment,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import AddPhotoAlternateIcon from "@mui/icons-material/AddPhotoAlternate";
import MenuItem from "@mui/material/MenuItem";
import CloseIcon from "@mui/icons-material/Close";
import { CATEGORY_OPTIONS } from "../constants/CategoryOptions";
import { PriceInput } from "../utils/PriceInput";
import UnprocessableEntity from "../err/UnprocessableEntity";

const MAX_IMAGES = 10;
const MAX_PRODUCTS = 5; // 사용자당 최대 상품 수

// 금지 품목 예시
const BANNED_WORDS = ["마약", "위조", "불법"];

const ProductRegisterPage = () => {
    const [form, setForm] = useState({
        title: "",
        description: "",
        price: "",
        category: "",
        images: [],
    });
    const [loading, setLoading] = useState(false);
    const [userProfile, setUserProfile] = useState({
        is_banned: "false",
        userProductCount: 0,
    });
    const fileInputRef = useRef();

    // accessToken에서 userId 추출
    const token = localStorage.getItem("accessToken");
    let userId = "";
    if (token) {
        try {
            const payload = jwtDecode(token);
            userId = payload.sub; // JWT의 sub가 userId
        } catch (e) {
            e.stopPropagation();
            userId = "";
        }
    }

    // 사용자 정보 불러오기
    useEffect(() => {
        window.scrollTo(0, 0);
        if (userId) {
        axios
            .get(`/api/users/${userId}/profile`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
            })
            .then((res) => {
            setUserProfile({
                is_banned: String(res.data.is_banned),
                userProductCount: res.data.userProductCount,
            });
            })
            .catch(() => {
            setUserProfile({
                is_banned: "false",
                userProductCount: 0,
            });
            });
        }
    }, [userId, token]);

    // 금지 품목 검사 함수
    const isBannedProduct = (title, description) => {
        return BANNED_WORDS.some(
        (word) => title.includes(word) || description.includes(word)
        );
    };

    // 이미지 파일 선택 핸들러
    const handleImageSelect = (e) => {
        const files = Array.from(e.target.files);
        if (form.images.length + files.length > MAX_IMAGES) {
        UnprocessableEntity(`이미지는 최대 ${MAX_IMAGES}장까지 등록할 수 있습니다.`);
        return;
        }
        const newImages = files.map((file) => ({
        file,
        url: URL.createObjectURL(file),
        }));
        setForm((prev) => ({
        ...prev,
        images: [...prev.images, ...newImages],
        }));
    };

    // 이미지 삭제
    const handleRemoveImage = (idx) => {
        setForm((prev) => ({
        ...prev,
        images: prev.images.filter((_, i) => i !== idx),
        }));
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // 1. 이미지 개수 검사
        if (form.images.length > MAX_IMAGES) {
        UnprocessableEntity(`이미지는 최대 ${MAX_IMAGES}장까지 등록할 수 있습니다.`);
        return;
        }

        // 2. 사용자당 등록 상품 최대 5개 검사
        if (userProfile.userProductCount >= MAX_PRODUCTS) {
        UnprocessableEntity("사용자당 등록 상품은 최대 5개입니다.");
        return;
        }

        // 3. 금지 품목 검사
        if (isBannedProduct(form.title, form.description)) {
        UnprocessableEntity("금지 품목은 등록할 수 없습니다.");
        return;
        }

        // 4. 정지 계정 검사
        if (userProfile.is_banned === "true") {
        UnprocessableEntity("정지된 계정은 상품을 등록할 수 없습니다.");
        return;
        }

        setLoading(true);
        try {
        const formData = new FormData();
        formData.append("title", form.title);
        formData.append("description", form.description);
        formData.append("price", form.price);
        formData.append("category", form.category);
        form.images.forEach((img) => {
            formData.append("images", img.file);
        });

        const res = await axios.post(
            `/api/users/${userId}/products`,
            formData,
            {
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "multipart/form-data",
            },
            }
        );
        if (res.data?.success) {
            alert("상품이 등록되었습니다.");
        }
        } catch (e) {
        if (e.response && e.response.status === 422) {
            UnprocessableEntity();
        } else {
            alert("상품 등록 중 오류가 발생했습니다.");
        }
        } finally {
        setLoading(false);
        }
    };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ width: 1100, mx: "auto", p: 3 }}>
        <Typography variant="h4" fontWeight={700} mb={4} ml={4} align="left">
            상품 등록
        </Typography>
        <Divider sx={{ mb: 4, borderColor: "#222", borderWidth: 2 }} />

        {/* 상품이미지 */}
        <Box sx={{ mb: 4 }}>
            <Box display="flex" alignItems="flex-start">
                {/* 상품이미지 텍스트 */}
                <Box
                    sx={{
                    display: "flex",
                    alignItems: "center",
                    minWidth: 120,
                    width: 120,
                    height: 200,
                    mr: 2,
                    flexShrink: 0,
                    }}
                >
                    <Typography fontWeight={700} fontSize={18}>
                    상품이미지{" "}
                    <Typography component="span" color="#888" fontWeight={400} display="inline">
                        ({form.images.length}/{MAX_IMAGES})
                    </Typography>
                    </Typography>
                </Box>
                {/* 이미지 등록 버튼 + 미리보기 이미지들 */}
                <Box sx={{ flex: 1, display: "flex", flexWrap: "wrap", gap: 2, alignItems: "flex-start" }}>
                    <Paper
                    variant="outlined"
                    sx={{
                        width: 200,
                        height: 200,
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        flexDirection: "column",
                        cursor: "pointer",
                        position: "relative",
                        minWidth: 200,
                        minHeight: 200,
                        bgcolor: "#fafafa",
                        borderRadius: 0,
                    }}
                    onClick={() => fileInputRef.current.click()}
                    >
                    <input
                        type="file"
                        accept="image/*"
                        multiple
                        ref={fileInputRef}
                        style={{ display: "none" }}
                        onChange={handleImageSelect}
                    />
                    <AddPhotoAlternateIcon sx={{ fontSize: 48, color: "#bbb" }} />
                    <Typography mt={1.5} fontSize={18} color="#888">
                        이미지 등록
                    </Typography>
                    </Paper>
                    {form.images.map((img, idx) => (
                    <Paper
                        key={idx}
                        variant="outlined"
                        sx={{
                        width: 200,
                        height: 200,
                        minWidth: 200,
                        minHeight: 200,
                        position: "relative",
                        overflow: "hidden",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        bgcolor: "#fafafa",
                        borderRadius: 0,
                        }}
                    >
                        {idx === 0 && (
                        <Box
                            sx={{
                            position: "absolute",
                            top: 8,
                            left: 8,
                            background: "rgba(0,0,0,0.6)",
                            color: "#fff",
                            fontSize: 13,
                            px: 1.5,
                            py: 0.5,
                            borderRadius: 2,
                            zIndex: 2,
                            }}
                        >
                            대표이미지
                        </Box>
                        )}
                        <Box
                        component="img"
                        src={img.url}
                        alt={`상품이미지${idx + 1}`}
                        sx={{
                            width: "100%",
                            height: "100%",
                            objectFit: "cover",
                        }}
                        />
                        <IconButton
                        size="small"
                        onClick={() => handleRemoveImage(idx)}
                        sx={{
                            position: "absolute",
                            top: 8,
                            right: 8,
                            background: "rgba(0,0,0,0.6)",
                            color: "#fff",
                            "&:hover": { background: "rgba(0,0,0,0.8)" },
                            zIndex: 2,
                        }}
                        >
                        <CloseIcon fontSize="medium" />
                        </IconButton>
                    </Paper>
                    ))}
                </Box>
            </Box>
        </Box>
        <Divider sx={{ my: 3, borderColor: "#eee", borderWidth: 1.5 }} />

        {/* 상품명 */}
        <Box sx={{ mb: 4, display: "flex", alignItems: "center", gap: 2 }}>
            <Typography fontWeight={700} sx={{ minWidth: 100 }}>
            상품명
            </Typography>
            <TextField
            name="title"
            value={form.title}
            onChange={handleChange}
            required
            fullWidth
            placeholder="상품명을 입력해 주세요."
            inputProps={{ maxLength: 40 }}
            sx={{ flex: 1 }}
            />
            <Typography variant="body2" color="#888" sx={{ ml: 1 }}>
            {form.title.length}/40
            </Typography>
        </Box>
        <Divider sx={{ my: 3, borderColor: "#eee", borderWidth: 1.5 }} />
        
        {/* 카테고리 */}
        <Box sx={{ mb: 4, display: "flex", alignItems: "flex-start", gap: 2 }}>
            <Typography fontWeight={700} sx={{ minWidth: 100, mt: 2 }}>
            카테고리
            </Typography>
            <Box sx={{ flex: 1 }}>
            <TextField
                select
                name="category"
                value={form.category}
                onChange={handleChange}
                fullWidth
                displayEmpty
                SelectProps={{
                displayEmpty: true,
                renderValue: selected =>
                    selected
                    ? CATEGORY_OPTIONS.find(opt => opt.value === selected)?.label
                    : "카테고리 선택",
                }}
            >
                <MenuItem value="" disabled>
                카테고리 선택
                </MenuItem>
                {CATEGORY_OPTIONS.map(opt => (
                <MenuItem key={opt.value} value={opt.value}>
                    {opt.label}
                </MenuItem>
                ))}
            </TextField>
            </Box>
        </Box>
        <Divider sx={{ my: 3, borderColor: "#eee", borderWidth: 1.5 }} />

        {/* 설명 */}
        <Box sx={{ mb: 4, display: "flex", alignItems: "flex-start", gap: 2 }}>
            <Typography fontWeight={700} sx={{ minWidth: 100, mt: 1 }}>
            설명
            </Typography>
            <TextField
            name="description"
            value={form.description}
            onChange={handleChange}
            required
            fullWidth
            multiline
            minRows={4}
            placeholder="상품 설명을 입력해 주세요."
            sx={{ flex: 1 }}
            />
        </Box>
        <Divider sx={{ my: 3, borderColor: "#eee", borderWidth: 1.5 }} />

        {/* 가격 */}
        <Box sx={{ mb: 4, display: "flex", alignItems: "center", gap: 2 }}>
            <Typography fontWeight={700} sx={{ minWidth: 100 }}>
                가격
            </Typography>
            <TextField
                name="price"
                type="text"
                value={form.price}
                onChange={e =>
                    setForm(prev => ({
                        ...prev,
                        price: PriceInput(e.target.value)
                    }))
                }
                required
                fullWidth
                InputProps={{
                    startAdornment: <InputAdornment position="start">₩</InputAdornment>,
                    inputProps: { min: 0 },
                }}
                sx={{ flex: 1 }}
            />
        </Box>

        {/* 등록 버튼 */}
        <Box display="flex" justifyContent="flex-end">
            <Button
                type="submit"
                variant="contained"
                color="primary"
                size="large"
                disabled={loading}
                sx={{ width: 180, height: 48, fontWeight: 700, fontSize: 18, mt: 2 }}
            >
            {loading ? "등록 중..." : "상품 등록"}
            </Button>
        </Box>
    </Box>
  );
};

export default ProductRegisterPage;