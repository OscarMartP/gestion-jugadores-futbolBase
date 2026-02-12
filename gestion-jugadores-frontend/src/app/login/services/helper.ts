import { environment } from '../../../environments/environment';

let baserUrl = environment.apiUrl.replace('/api/v1', '');
export default baserUrl;