#ifndef MUDPF_FITZ_H
#define MUDPF_FITZ_H

#ifdef __cplusplus
extern "C" {
#endif

#include "mupdf/include/mupdf/fitz/version.h"
#include "mupdf/include/mupdf/fitz/system.h"
#include "mupdf/include/mupdf/fitz/context.h"

#include "mupdf/include/mupdf/fitz/crypt.h"
#include "mupdf/include/mupdf/fitz/getopt.h"
#include "mupdf/include/mupdf/fitz/hash.h"
#include "mupdf/include/mupdf/fitz/math.h"
#include "mupdf/include/mupdf/fitz/pool.h"
#include "mupdf/include/mupdf/fitz/string.h"
#include "mupdf/include/mupdf/fitz/tree.h"
#include "mupdf/include/mupdf/fitz/ucdn.h"
#include "mupdf/include/mupdf/fitz/bidi.h"
#include "mupdf/include/mupdf/fitz/xml.h"

/* I/O */
#include "mupdf/include/mupdf/fitz/buffer.h"
#include "mupdf/include/mupdf/fitz/stream.h"
#include "mupdf/include/mupdf/fitz/compressed-buffer.h"
#include "mupdf/include/mupdf/fitz/filter.h"
#include "mupdf/include/mupdf/fitz/output.h"
#include "mupdf/include/mupdf/fitz/unzip.h"

/* Resources */
#include "mupdf/include/mupdf/fitz/store.h"
#include "mupdf/include/mupdf/fitz/colorspace.h"
#include "mupdf/include/mupdf/fitz/pixmap.h"
#include "mupdf/include/mupdf/fitz/glyph.h"
#include "mupdf/include/mupdf/fitz/bitmap.h"
#include "mupdf/include/mupdf/fitz/image.h"
#include "mupdf/include/mupdf/fitz/function.h"
#include "mupdf/include/mupdf/fitz/shade.h"
#include "mupdf/include/mupdf/fitz/font.h"
#include "mupdf/include/mupdf/fitz/path.h"
#include "mupdf/include/mupdf/fitz/text.h"
#include "mupdf/include/mupdf/fitz/separation.h"

#include "mupdf/include/mupdf/fitz/device.h"
#include "mupdf/include/mupdf/fitz/display-list.h"
#include "mupdf/include/mupdf/fitz/structured-text.h"

#include "mupdf/include/mupdf/fitz/transition.h"
#include "mupdf/include/mupdf/fitz/glyph-cache.h"

/* Document */
#include "mupdf/include/mupdf/fitz/link.h"
#include "mupdf/include/mupdf/fitz/outline.h"
#include "mupdf/include/mupdf/fitz/document.h"
#include "mupdf/include/mupdf/fitz/annotation.h"

#include "mupdf/include/mupdf/fitz/util.h"

/* Output formats */
#include "mupdf/include/mupdf/fitz/output-pnm.h"
#include "mupdf/include/mupdf/fitz/output-png.h"
#include "mupdf/include/mupdf/fitz/output-pwg.h"
#include "mupdf/include/mupdf/fitz/output-pcl.h"
#include "mupdf/include/mupdf/fitz/output-ps.h"
#include "mupdf/include/mupdf/fitz/output-svg.h"
#include "mupdf/include/mupdf/fitz/output-tga.h"

#ifdef __cplusplus
}
#endif

#endif
