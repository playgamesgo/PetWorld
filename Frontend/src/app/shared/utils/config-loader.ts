import { DictionaryService } from '../../services/dictionary.service';
import { Router } from '@angular/router';
import { ROUTING_LIST } from '../../app.config';

const configLoader = (dictionaryService: DictionaryService, router: Router) => async () => await dictionaryService.loadDictionary();

export default configLoader;
