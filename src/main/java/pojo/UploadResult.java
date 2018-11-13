package pojo;

/**
 * @author Hasaghi
 * @date 13/11/2018
 */
public class UploadResult {
	public int size;
	public String downloadLink;

	public UploadResult(int size, String downloadLink) {
		this.size = size;
		this.downloadLink = downloadLink;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getDownloadLink() {
		return downloadLink;
	}

	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}

	@Override
	public String toString() {
		return "UploadResult{" +
				"size=" + size +
				", downloadLink='" + downloadLink + '\'' +
				'}';
	}
}
