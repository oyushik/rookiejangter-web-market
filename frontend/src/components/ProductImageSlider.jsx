import { Box, IconButton, Typography } from "@mui/material";
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';

const ProductImageSlider = ({ images, imgIdx, onPrev, onNext, title }) => {
  const hasImages = images && images.length > 0;
  return (
    <Box
      sx={{
        width: 400,
        aspectRatio: '1/1',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        bgcolor: '#fafafa',
        borderRadius: 2,
        overflow: 'hidden',
        position: 'relative',
        flexShrink: 0,
      }}
    >
      {hasImages ? (
        <>
          <img
            src={images[imgIdx]}
            alt={title}
            style={{ width: '100%', height: 'auto', objectFit: 'contain', maxHeight: 400 }}
          />
          {images.length > 1 && (
            <>
              <IconButton
                onClick={onPrev}
                disabled={imgIdx === 0}
                sx={{
                  position: 'absolute',
                  left: 8,
                  top: '50%',
                  transform: 'translateY(-50%)',
                  bgcolor: 'rgba(255,255,255,0.7)',
                  '&:hover': { bgcolor: 'rgba(255,255,255,0.9)' }
                }}
                size="small"
              >
                <ArrowBackIosNewIcon />
              </IconButton>
              <IconButton
                onClick={onNext}
                disabled={imgIdx === images.length - 1}
                sx={{
                  position: 'absolute',
                  right: 8,
                  top: '50%',
                  transform: 'translateY(-50%)',
                  bgcolor: 'rgba(255,255,255,0.7)',
                  '&:hover': { bgcolor: 'rgba(255,255,255,0.9)' }
                }}
                size="small"
              >
                <ArrowForwardIosIcon />
              </IconButton>
              <Box
                sx={{
                  position: 'absolute',
                  bottom: 8,
                  left: '50%',
                  transform: 'translateX(-50%)',
                  bgcolor: 'rgba(0,0,0,0.5)',
                  color: '#fff',
                  borderRadius: 2,
                  px: 1.5,
                  fontSize: 13,
                  py: 0.2,
                }}
              >
                {imgIdx + 1} / {images.length}
              </Box>
            </>
          )}
        </>
      ) : (
        <Typography color="text.secondary">이미지 없음</Typography>
      )}
    </Box>
  );
};

export default ProductImageSlider;